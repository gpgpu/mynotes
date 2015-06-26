var nsEditArticle = nsEditArticle || {};




nsEditArticle.articleId = window.opener.targetArticleId;
nsEditArticle.isLocalNew = false;
nsEditArticle.modifiedOn;
nsEditArticle.locallyModified = false;

var httpConfig = {headers:{
   'x-auth-token': sessionStorage.token
}}

  	 InitInlineStyleButtons();
     InitBlockStyleButtons();
     setEventListners();

     app.localdb.openDb(function(db){
        app.localdb.getArticle(nsEditArticle.articleId, function(article){
            if (article.status === app.status.ok){
             document.title = article.entity.name;
             nsEditArticle.modifiedOn = article.entity.modifiedOn;
             nsEditArticle.locallyModified = article.entity.locallyModified;
             $("#articleName").text(article.entity.name);
               $("#contentArea").html(article.entity.content);
               if($("#contentArea").html() == ""){
                $("#contentArea").html("<p>a</p><p></p>");
              }
            }
          else{
            var url = "rest/article/" + nsEditArticle.articleId;

            $.ajax({
                url: url,
                headers: { 'x-auth-token': sessionStorage.token },
                type: 'GET'
            }).done(function(data){
                document.title = data.name;
                nsEditArticle.modifiedOn = data.modifiedOn;
                $("#articleName").text(data.name);
                $("#contentArea").html(data.content);
                if($("#contentArea").html() == ""){
                    $("#contentArea").html("<p>a</p><p></p>");
                }
                app.localdb.addArticle(data);
            });
          }
        });
     });

        $("#diaChangeHeight").dialog({
            autoOpen: false,
            modal: true,
            width: 300,
            height: 300
        });

        $("#dialogInsertTable").dialog({
            autoOpen: false,
            modal: true,
            width: 300,
            height: 300

        });

        $("#dialogInsertImage").dialog({
            autoOpen: false,
            modal: true,
            width: 400,
            height: 150,
            open: function(){
                $.ajaxSetup({cache: false});
                $("#fileInputContainer").html('<form enctype="multipart/form-data"><input type="file" id="fileUploader" name="theFile" /><br /><input type="button" id="btnInsertImage" value="Insert" /><input type="button" id="btnCancelImageDialog" value="Cancel" /></form>');
            }

        });

        HeightSetReturnAsDefault();

  	function InitInlineStyleButtons(){
        $("#btnBold").click(function(){ editStyle('bold'); });
        $("#btnUnderline").click(function(){ editStyle('underline');});
        $("#btnItalize").click(function(){ editStyle('italic');});
    }
    function InitBlockStyleButtons(){
        $("#btnHeading1").click(function(){ApplyBlockStyle('h1');});
        $("#btnHeading2").click(function(){ApplyBlockStyle('h2');});
        $("#btnHeading3").click(function(){ApplyBlockStyle('h3');});
        $("#btnNormal").click(function(){ApplyBlockStyle('p');});
        $("#btnUl").click(function(){insertTag('ul');});
        $("#btnOl").click(function(){ insertTag('ol');});
    }
    function setEventListners(){
        $(document).on("click", "#btnUnescape", function(){
            var rawContents = $("#contentArea").html();
            $("#contentArea").html(unescape(rawContents));
         });

         $('#contentArea').keydown(function(event){
             if (event.keyCode == 9){
                 event.preventDefault();
                 var range = window.getSelection().getRangeAt(0);
                 range.deleteContents();
                 range.insertNode(document.createTextNode("  "));

             }
         });

          $('#btnTable').click(function(){

             var range = window.getSelection().getRangeAt(0);

             $("#dialogInsertTable").data('ran', range).dialog('open');
         });

         $('#btnCreateTable').click(function(){
             var ran =  $("#dialogInsertTable").data('ran');

             var rows = Number($('#tbxTableRows').val());
             var cols = Number($('#tbxTableColumns').val());
             var hasHeader = $('#cbxHasHeader:checked').length;


             var tableHTML = '<table>';
             if (hasHeader > 0){
                 var headerRow = '<tr>';
                 for (colCount=0; colCount < cols; colCount++){
                     headerRow = headerRow + '<th></th>';
                 }
                 headerRow = headerRow + '</tr>';
             }
             tableHTML = tableHTML + headerRow;
             for (rowCount = 0; rowCount < rows; rowCount++){
                 var rowHtml = '<tr>';
                 for (colCount=0; colCount < cols; colCount++){
                     rowHtml = rowHtml + '<td></td>';
                 }
                 rowHtml = rowHtml + '</tr>';
                 tableHTML = tableHTML + rowHtml;
             }
             tableHTML = tableHTML + '</table>';

             var newElement = document.createElement('div');
             newElement.innerHTML = tableHTML;

             ran.deleteContents();
             ran.insertNode(newElement);

             $("#dialogInsertTable").dialog('close');
         });

         $('#btnCancelTable').click(function(){
             $("#dialogInsertTable").dialog('close');
         });

         $('#btnOpenImageDialog').click(function () {
                     var range = window.getSelection().getRangeAt(0);
                     //   alert(range);
                     $("#dialogInsertImage").data("range", range).dialog('open');

                 });

         $(document).on("click", "#btnCancelImageDialog", function () {
             $("#dialogInsertImage").dialog('close');
         });
         $(document).on("click", "#btnInsertImage", function () {
             var theFile = document.getElementById("fileUploader").files[0];
             if (!theFile) return;
             var formData = new FormData();
             //		formData.append(theFile.name, theFile);
             formData.append("fileUploader", theFile);

             var request = new XMLHttpRequest();
             request.open('POST', '/rest/file');
             //      request.setRequestHeader("Content-Type", "multipart/form-data");
             request.setRequestHeader("X-File-Name", theFile.name);
             request.setRequestHeader("X-File-Size", theFile.size);
             request.setRequestHeader("X-File-Type", theFile.type);
             request.setRequestHeader("X-File-ArticleId", nsEditArticle.articleId);
             request.setRequestHeader("x-auth-token", sessionStorage.token);

             request.onreadystatechange = function () { // Simple event handler
                 if (request.readyState === 4){
                     //   alert(request.responseText);

                     var range = $("#dialogInsertImage").data("range");
                     var theId = request.responseText;
                     var src = '/rest/file/unsecured/' + theId;
                     insertImg(range, src, theId);

                     $("#dialogInsertImage").dialog('close');
                 }
             };

             request.send(formData);
         });
         $(document).on('change', '#paragraph', function(){
             var selectedVal = $('#paragraph').val();
             switch (selectedVal){
                 case 'heading1':
                     ApplyBlockStyle('h1');
                     break;
                 case 'heading2':
                     ApplyBlockStyle('h2');
                     break;
                 case 'heading3':
                     ApplyBlockStyle('h3');
                     break;
                 case 'heading4':
                     ApplyBlockStyle('h4');
                     break;
                 case 'normal':
                     ApplyBlockStyle('p');
                     break;
             }
             $('#paragraph').val('-');
         });

         $(document).on('change', '#coding', function(){
             var selectedVal = $('#coding').val();
             switch (selectedVal){
                 case 'code':
                     //	insertTag('code');
                     formatCode('code');
                     break;
                 case 'badCode':
                     insertTag('badCode');
                     break;
                 case 'script':
                     insertTag('script');
                     break;
             }
             $('#coding').val('-');

         });

         $(document).on("click", "#btnSaveLocal", function(){
            var imageUrl = "<img src='images/busy_indicator.gif' alt='busy' style='width:16px; height:16px' />";

             $('#saveIndicator').html(imageUrl);
             $('#saveIndicator').show();

             app.localdb.getArticle(nsEditArticle.articleId, function(article){
                 article.entity.content = $("#contentArea").html();
                 article.entity.locallyModified = true;

                 app.localdb.updateArticle(article.entity, function(){
                    nsEditArticle.locallyModified = true;
                     $('#saveIndicator').html("<span style='color:green;'>Saved</span>").fadeOut(3000);
                 })
             });
         });

         $(document).on("click", "#btnSaveContent", function(){
            var url = "rest/article/content";
             updateContent(url);
         });

         $(document).on("click", "#btnHTML", function(){
             var theHtml = $("#contentArea").html().replace(/&lt;/g, '&amp;lt;').replace(/&gt;/g, '&amp;gt;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
             $("#contentArea").html(theHtml);
         });
         $(document).on("click", "#btnText", function(){
             var theHtml = $("#contentArea").html().replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;lt;/g, '&lt;').replace(/&amp;gt;/g, '&gt;');
             $("#contentArea").html(theHtml);
         });
         $(document).on("click", "#btnClear", function(){
             if (confirm("All contents will be discarded, continue?"))
                 $("#contentArea").html('<p>a</p><p></p>');
         });
         $(document).on("click", "#btnGetLatest", function(){
            if (nsEditArticle.locallyModified == true){
                alert("Contents have been modified locally. Push to server first.");
                return;
            }
            var param = {};
             param.articleId = nsEditArticle.articleId;
             param.modifiedOn = nsEditArticle.modifiedOn;

             var destinationURL = "rest/article/sync"

             var imageUrl = "<img src='images/busy_indicator.gif' alt='busy' style='width:16px; height:16px' />";

             $('#saveIndicator').html(imageUrl);
             $('#saveIndicator').show();

             $.ajax({
                 url: destinationURL,
                 headers: { 'x-auth-token': sessionStorage.token },
                 type: 'POST',
                 data: param
             }).done(function(data, status){
                 if (status == "nocontent"){
                     $('#saveIndicator').html("<span style='color:green;'>No update needed</span>").fadeOut(3000);
                 }
                 else if (status == "success"){
                     app.localdb.updateArticle(data, function(){
                         nsEditArticle.modifiedOn = data.modifiedOn;
                          $("#contentArea").html(data.content);
                         if($("#contentArea").html() == ""){
                             $("#contentArea").html("<p>a</p><p></p>");
                         }
                         $('#saveIndicator').html("<span style='color:green;'>Updated!</span>").fadeOut(3000);
                     })
                 }

             });
         });
         $(document).on("click", "#btnShowServerVersion", function(event){
            event.preventDefault();
            var newWindow = window.open("newWindow.html", "popupWindow", "width=1200,height=800,scrollbars=yes");

            newWindow.onload = function(){
                var url = "rest/article/" + nsEditArticle.articleId;

                $.ajax({
                    url: url,
                    headers: { 'x-auth-token': sessionStorage.token },
                    type: 'GET'
                }).done(function(data){
                    newWindow.dataFromServer = data.content;
                    newWindow.init();
                });
            };
         });
         $(document).on("click", "#btnForceUpdate", function(event){
            var url = "rest/article/content?forceupdate=true";
            updateContent(url);

         });
     }
    function replaceSelectedText(replacementText) {
        var sel, range;
        if (window.getSelection) {
            sel = window.getSelection();
            if (sel.rangeCount) {
                range = sel.getRangeAt(0);
                range.deleteContents();
                range.insertNode(document.createTextNode(replacementText));
            }
        } else if (document.selection && document.selection.createRange) {
            range = document.selection.createRange();
            range.text = replacementText;
        }
    }
    function updateContent(url){
        var imageUrl = "<img src='images/busy_indicator.gif' alt='busy' style='width:16px; height:16px' />";

         $('#saveIndicator').html(imageUrl);
         $('#saveIndicator').show();

         var para = {};
         para.id = nsEditArticle.articleId;
         para.content = $("#contentArea").html();
         para.modifiedOn = nsEditArticle.modifiedOn;

         $.ajax({
             type: "PUT",
             url: url,
             headers: { 'x-auth-token': sessionStorage.token },
             data: JSON.stringify(para),

             contentType: "application/json",
             success: function (response, status, xhr) {
                 console.log(response);
                 console.log(status);
                 if (status == "success"){
                    app.localdb.getArticle(nsEditArticle.articleId, function(article){
                        article.entity.modifiedOn = response;
                        article.entity.locallyModified = false;
                        nsEditArticle.modifiedOn = response;
                        nsEditArticle.locallyModified = false;
                        app.localdb.updateArticle(article.entity, function(){
                            $('#saveIndicator').html("<span style='color:green;'>Saved</span>").fadeOut(3000);
                        })
                    });
                 }
                 else if (status == "nocontent"){
                    $('#saveIndicator').html("<span style='color:red;'>version conflict</span>").fadeOut(3000);
                 }
                 else{
                    alert("status: " + status);
                 }

             },
             error: function (xhr, error) {
                 $('#saveIndicator').html("<span style='color:red;'>Saving Failed</span>" + xhr.responseText);
             }
         });
    }
    function btnShowHeightDialog_Click(){
        // get current height value
        $("input#tbxNewHeight", "#diaChangeHeight").val($("#contentArea").css("max-height"));
        $("#diaChangeHeight").dialog('open');
    }
    function btnSaveNewHeight_Click(){
        // need validation for numbers / ranges here

        // actually this dialog may not be needed if the window.resize event can be detected and hooked up with a function that change the div size accordingly.

        // and the dialog can be a multi-function panel.

        $("#contentArea").css("max-height", $('#tbxNewHeight').val()/*+'px'*/);
        $("#diaChangeHeight").dialog('close');
    }

    function HeightSetReturnAsDefault(){
        $("#tbxNewHeight").keydown(function(ev){
            if (ev.keyCode == 13){
                btnSaveNewHeight_Click();
            }
        });
    }
