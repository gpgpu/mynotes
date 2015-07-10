/// <reference path="../jquery-1.4.4-vsdoc.js" />

function ApplyBlockStyle(styleName) {
    // get the anchor for selected text
    var anchor = window.getSelection().anchorNode.parentNode;

    var blockanchor = anchor;

    while (blockanchor.nodeName != "BODY" && blockanchor.nodeName != "P" && blockanchor.nodeName != "H1" && blockanchor.nodeName != "H2" && blockanchor.nodeName != "H3") {
        blockanchor = blockanchor.parentNode;
    }

    if (blockanchor.nodeName != "BODY") {
        var c = document.createElement(styleName);
        c.innerHTML = blockanchor.innerHTML;
        blockanchor.parentNode.replaceChild(c, blockanchor);
    }

    return false;
}

function editStyle(cmd) {
    document.execCommand(cmd, null, null);
}
function formatCode(codeStyle){
	var sel = window.getSelection();;
	var range = sel.getRangeAt(0);
	var contents = sel.toString().replace(/</g, '&lt;').replace(/>/g, '&gt;');
//	console.log(contents);
	var theElement = document.createElement("pre");
	theElement.className=codeStyle;
	theElement.innerHTML = contents;
	range.deleteContents();
	range.insertNode(theElement);
	sel.removeAllRanges();
	sel.addRange(range);
}

function insertTag(tagName) {

    if (window.getSelection) {
        var sel = window.getSelection();
        if (sel.getRangeAt && sel.rangeCount) {
            var range = sel.getRangeAt(0);
            var container = range.startContainer;
            var blockParent = findBlockLevelParent(container);
            var grandParent;
            if (blockParent.nodeName == "BODY"){
                // do something here
            } 
            var grandParent = blockParent.parentNode;
            switch (tagName) {
                case 'code':
                    var theElement = document.createElement("pre")
                    theElement.innerHTML = blockParent.innerHTML;
                    grandParent.replaceChild(theElement, blockParent);
                    
                    //var selectionContents = range.extractContents();
                    //var theElement = document.createElement("pre");
                    //theElement.appendChild(selectionContents);
                    //range.insertNode(theElement);
                    break;
                case 'badCode':
                	var badCodeElement = document.createElement("pre");
                	badCodeElement.className='badCode';
                	badCodeElement.innerHTML = blockParent.innerHTML;
                    grandParent.replaceChild(badCodeElement, blockParent);
                	break;
                case 'script':
                	var scriptElement = document.createElement("pre");
                	scriptElement.className='script';
                	scriptElement.innerHTML = blockParent.innerHTML;
                    grandParent.replaceChild(scriptElement, blockParent);
                	break;
                case 'ul':
                    var ulElement = document.createElement("ul");
                    var liElement = document.createElement("li");
                    liElement.innerHTML = blockParent.innerHTML;
                    ulElement.appendChild(liElement);
                    grandParent.replaceChild(ulElement, blockParent);
                    
                    //var selectionContents = range.extractContents();
                    //var ulElement = document.createElement("ul");
                    //var liElement = document.createElement("li");
                    //liElement.appendChild(selectionContents);
                    //ulElement.appendChild(liElement);
                    //range.insertNode(ulElement);
                    break;
                case 'ol':
                    var olElement = document.createElement("ol");
                    var liElement = document.createElement("li");
                    liElement.innerHTML = blockParent.innerHTML;
                    olElement.appendChild(liElement);
                    grandParent.replaceChild(olElement, blockParent);
                    //var selectionContents = range.extractContents();
                    //var olElement = document.createElement("ol");
                    //var liElement = document.createElement("li");
                    //liElement.appendChild(selectionContents);
                    //olElement.appendChild(liElement);
                    //range.insertNode(olElement);
                    break;
                default:
                    range.deleteContents();
                    range.insertNode(document.createElement(tagName));
                    break;
            }
        }
    }
}

// imageId: for deleting purpose
function insertImg(range, src, imageId) {
    if (range) {
        range.deleteContents();
        var divContainer = document.createElement("div");
        divContainer.setAttribute('class', 'divImageHolder');

        var img = document.createElement("img");
        img.setAttribute('src', src);
        img.setAttribute('alt', 'img');
        img.setAttribute('id', imageId);

        divContainer.appendChild(img);

        range.deleteContents();
        range.insertNode(divContainer);

    }
}

function findBlockLevelParent(node) {
    if (isBlockNode(node))
        return node;
    var parent = node.parentNode;
    while (!isBlockNode(parent)) {
        parent = parent.parentNode;
    }
    return parent;
};

function isBlockNode(node){
    var name = node.nodeName;

    return name == "P" || name == "DIV" || name == "BODY" || name == "H1" || name == "H2" || name == "H3" || name == "PRE";
};
