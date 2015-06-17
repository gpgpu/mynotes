var app = app || {};

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

        req.onsuccess = function(event){
        };
        req.onerror = function(event){
        };

    };

    return {
        getARticle: getArticle
    };

}());





