 alert('I am here');

    if (!window.indexedDB){
        alert("indexedDB not avaliable")
    }
    else{
        alert("db is ok")
    }

var request = window.indexedDB.open("mynotes", 1);

request.onerror = function(event){
    alert("oops");
};
request.onsuccess = function(event){

}
request.onupgradeneeded = function(event){
    var db = event.target.result;
    var objectStore = db.createObjectStore("articles", {keyPath: "id"});

    objectStore.transaction.oncomplete = function(event){
        var articleObjectStore = db.transaction("articles", "readwrite").objectStore("articles");
        articleObjectStore.add({"id": 1, "content": "fuck"});
    }
};