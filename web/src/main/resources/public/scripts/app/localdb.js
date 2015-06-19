var app = app || {};

app.status = {
    ok: 1,
    notfound: 2
}

app.localdb = (function(){
    if (!window.indexedDB){
        alert("indexedDB not avaliable")
    }
    var db;
    var request = window.indexedDB.open("mynotes", 1);
    request.onerror = function(event){
        alert("oops");
    };
    request.onsuccess = function(event){
        db = event.target.result;
    }
    request.onupgradeneeded = function(event){
        var db = event.target.result;
        var objectStore = db.createObjectStore("articles", {keyPath: "id"});
    };

    var getArticle = function(id){
        var transaction = db.transaction("articles", "readonly");
        var store = transaction.objectStore("articles");
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
    };

    var saveArticle = function(article){
        var localCopy = getArticle(article.id);

        var store = db.transaction("articles", "readwrite").objectStore("articles");
        if (localCopy.status === app.status.ok){
            localCopy.content = article.content;
            var requestUpdate = store.put(localCopy);
        }
        else{
            store.add(article);
        }
    }

    return {
        getArticle: getArticle,
        saveArticle: saveArticle
    };

}());





