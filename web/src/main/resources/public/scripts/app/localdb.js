var app = app || {};

app.status = {
    ok: 1,
    notfound: 2
}
 app.localdb = (function () {
    const DB_NAME = 'mynotes';
    const DB_VERSION = 1; // Use a long long for this value (don't use a float)
    const DB_STORE_NAME = 'articles';

    var db;
    var store;

    /**
     * @param {string} store_name
     * @param {string} mode either "readonly" or "readwrite"
     */
    function getObjectStore(store_name, mode) {
        console.log("getObjectStore...");
        var tx = db.transaction(store_name, mode);
        console.log("getObjectStore done");
        store = tx.objectStore(store_name);
        return tx.objectStore(store_name);
    }

    function clearObjectStore(store_name) {
        var store = getObjectStore(DB_STORE_NAME, 'readwrite');
        var req = store.clear();
        req.onsuccess = function(evt) {
            displayActionSuccess("Store cleared");
            displayPubList(store);
        };
        req.onerror = function (evt) {
            console.error("clearObjectStore:", evt.target.errorCode);
            displayActionFailure(this.error);
        };
    }

    function getBlob(key, store, success_callback) {
        var req = store.get(key);
        req.onsuccess = function(evt) {
            var value = evt.target.result;
            if (value)
                success_callback(value.blob);
        };
    }

    /**
     * @param {IDBObjectStore=} store
     */
    function getArticle(id) {
        console.log("getArticle");
        if (typeof db == 'undefined'){
            console.log("openDb ...");
            var req = indexedDB.open(DB_NAME, DB_VERSION);
            req.onsuccess = function (evt) {
                // Better use "this" than "req" to get the result to avoid problems with
                // garbage collection.
                // db = req.result;
                db = this.result;
                console.log("openDb DONE");

                if (typeof store == 'undefined')
                    store = getObjectStore(DB_STORE_NAME, 'readonly');

            };
            req.onerror = function (evt) {
                console.error("openDb:", evt.target.errorCode);
            };

            req.onupgradeneeded = function (evt) {
                console.log("openDb.onupgradeneeded");
                var db = event.target.result;
                var objectStore = db.createObjectStore("articles", {keyPath: "id"});
            };
        }
        else{
            if (typeof store == 'undefined')
                store = getObjectStore(DB_STORE_NAME, 'readonly');
        }


        var req = store.get(id);

        var result;
        req.onsuccess = function(event){
            result = {
                status: app.status.ok,
                entity: request.result
            }
        };
        req.onerror = function(event){
            result = {
                status: app.status.notfound,
                message: "not found"
            }
        };
        return result;
    }



    /**
     * @param {string} biblioid
     * @param {string} title
     * @param {number} year
     * @param {string} url the URL of the image to download and store in the local
     *   IndexedDB database. The resource behind this URL is subjected to the
     *   "Same origin policy", thus for this method to work, the URL must come from
     *   the same origin as the web site/app this code is deployed on.
     */
    function addPublicationFromUrl(biblioid, title, year, url) {
        console.log("addPublicationFromUrl:", arguments);

        var xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        // Setting the wanted responseType to "blob"
        // http://www.w3.org/TR/XMLHttpRequest2/#the-response-attribute
        xhr.responseType = 'blob';
        xhr.onload = function (evt) {
            if (xhr.status == 200) {
                console.log("Blob retrieved");
                var blob = xhr.response;
                console.log("Blob:", blob);
                addPublication(biblioid, title, year, blob);
            } else {
                console.error("addPublicationFromUrl error:",
                    xhr.responseText, xhr.status);
            }
        };
        xhr.send();

        // We can't use jQuery here because as of jQuery 1.8.3 the new "blob"
        // responseType is not handled.
        // http://bugs.jquery.com/ticket/11461
        // http://bugs.jquery.com/ticket/7248
        // $.ajax({
        //   url: url,
        //   type: 'GET',
        //   xhrFields: { responseType: 'blob' },
        //   success: function(data, textStatus, jqXHR) {
        //     console.log("Blob retrieved");
        //     console.log("Blob:", data);
        //     // addPublication(biblioid, title, year, data);
        //   },
        //   error: function(jqXHR, textStatus, errorThrown) {
        //     console.error(errorThrown);
        //     displayActionFailure("Error during blob retrieval");
        //   }
        // });
    }

    /**
     * @param {string} biblioid
     * @param {string} title
     * @param {number} year
     * @param {Blob=} blob
     */
    function addPublication(biblioid, title, year, blob) {
        console.log("addPublication arguments:", arguments);
        var obj = { biblioid: biblioid, title: title, year: year };
        if (typeof blob != 'undefined')
            obj.blob = blob;

        var store = getObjectStore(DB_STORE_NAME, 'readwrite');
        var req;
        try {
            req = store.add(obj);
        } catch (e) {
            if (e.name == 'DataCloneError')
                displayActionFailure("This engine doesn't know how to clone a Blob, " +
                "use Firefox");
            throw e;
        }
        req.onsuccess = function (evt) {
            console.log("Insertion in DB successful");
            displayActionSuccess();
            displayPubList(store);
        };
        req.onerror = function() {
            console.error("addPublication error", this.error);
            displayActionFailure(this.error);
        };
    }

    /**
     * @param {string} biblioid
     */
    function deletePublicationFromBib(biblioid) {
        console.log("deletePublication:", arguments);
        var store = getObjectStore(DB_STORE_NAME, 'readwrite');
        var req = store.index('biblioid');
        req.get(biblioid).onsuccess = function(evt) {
            if (typeof evt.target.result == 'undefined') {
                displayActionFailure("No matching record found");
                return;
            }
            deletePublication(evt.target.result.id, store);
        };
        req.onerror = function (evt) {
            console.error("deletePublicationFromBib:", evt.target.errorCode);
        };
    }

    /**
     * @param {number} key
     * @param {IDBObjectStore=} store
     */
    function deletePublication(key, store) {
        console.log("deletePublication:", arguments);

        if (typeof store == 'undefined')
            store = getObjectStore(DB_STORE_NAME, 'readwrite');

        // As per spec http://www.w3.org/TR/IndexedDB/#object-store-deletion-operation
        // the result of the Object Store Deletion Operation algorithm is
        // undefined, so it's not possible to know if some records were actually
        // deleted by looking at the request result.
        var req = store.get(key);
        req.onsuccess = function(evt) {
            var record = evt.target.result;
            console.log("record:", record);
            if (typeof record == 'undefined') {
                displayActionFailure("No matching record found");
                return;
            }
            // Warning: The exact same key used for creation needs to be passed for
            // the deletion. If the key was a Number for creation, then it needs to
            // be a Number for deletion.
            req = store.delete(key);
            req.onsuccess = function(evt) {
                console.log("evt:", evt);
                console.log("evt.target:", evt.target);
                console.log("evt.target.result:", evt.target.result);
                console.log("delete successful");
                displayActionSuccess("Deletion successful");
                displayPubList(store);
            };
            req.onerror = function (evt) {
                console.error("deletePublication:", evt.target.errorCode);
            };
        };
        req.onerror = function (evt) {
            console.error("deletePublication:", evt.target.errorCode);
        };
    }

    openDb();

    return {
        getArticle: getArticle
    }

})(); // Immediately-Invoked Function Expression (IIFE)



