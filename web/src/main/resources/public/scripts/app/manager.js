var refBookApp = angular.module('refBookApp', []);

var enumItemType = {
    portal: 1,
    topic: 2,
    article: 3
};
//var ContentItem = function (originalItem, name, displayOrder) {
//    var self = this;
//    self.name = name;
//    self.originalItem = originalItem;
//    self.displayOrder = displayOrder;
//    
//};
//ContentItem.prototype.clickItem = function () {
//    alert(this.name);
//};

var Portal = function (id, title) {
    var self = this;
    self.id = id;
    self.title = title;
    self.expanded = false;
    self.isSelected = false;
    self.topics = [];
    
};
Portal.prototype.itemType = enumItemType.portal;
Portal.prototype.toggleExpansion = function () {
    this.expanded = !(this.expanded);
};
Portal.prototype.handleIndicator = function () {
    if (this.topics.length == 0) return ' ';
    if (this.expanded) return '-';
    return '+';
};
Portal.prototype.removeTopic = function (topic){
    if (this.topics && this.topics.length){
        for (var i = 0; i < this.topics.length; i++){
            if (this.topics[i].id == topic.id){
                this.topics.splice(i, 1);
                break;
            }
        }
    }
};
Portal.prototype.addTopic = function (topic) {
    this.topics.push(topic);
};

var Topic = function (topicId, portalId, parent, title, displayOrder) {
    var self = this;

    self.id = topicId;
    self.portalId = portalId;
    self.parent = parent;
    self.title = title;
    self.displayOrder = displayOrder;

    self.expanded = false;
    self.isSelected = false;
    self.showCommandsMode = false;
    self.topics = [];
    self.articles = [];

    
};
Topic.prototype.itemType = enumItemType.topic;
Topic.prototype.hasChildren = function () {
    return this.topics && this.topics.length > 0;
};
Topic.prototype.toggleExpansion = function () {
    this.expanded = !(this.expanded);
//	this.expanded = true;
};
Topic.prototype.handleIndicator = function () {
    if (!this.hasChildren()) return ' ';
    if (this.expanded) return '-';
    return '+';
};
Topic.prototype.removeTopic = function (topic){
    if (this.topics && this.topics.length){
        for (var i = 0; i < this.topics.length; i++){
            if (this.topics[i].id == topic.id){
                this.topics.splice(i, 1);
                break;
            }
        }
    }
};
Topic.prototype.removeArticle = function (article) {
    if (this.articles && this.articles.length) {
        for (var i = 0; i < this.articles.length; i++) {
            if (this.articles[i].id == article.id) {
                this.articles.splice(i, 1);
                break;
            }
        }
    }
};
Topic.prototype.isChildOf = function (target) {
    if (target.itemType == enumItemType.article || target.itemType == enumItemType.portal)
        return false;
    var targetIsParent = false;
    var theParent = this.parent;

    if (target.itemType == enumItemType.topic && theParent.id == target.id) {
        targetIsParent = true;

    }
    else if (!theParent || theParent.itemType == enumItemType.portal) {
        targetIsParent = false;
    }
    else {
        targetIsParent = theParent.isChildOf(target);
    }

    return targetIsParent;
};
Topic.prototype.addTopic = function (topic) {
    
        this.topics.push(topic);
    
};
Topic.prototype.addArticle = function (article) {
    
    this.articles.push(article);
    
};
Topic.prototype.toggleShowCommandsMode = function(){
	this.showCommandsMode = !(this.showCommandsMode);
}

var Article = function (parent, articleId, title, displayOrder) {
    var self = this;
    self.parent = parent;
    self.id = articleId;
    self.title = title;
    self.displayOrder = displayOrder;
    self.showCommandsMode = false;
    
};
Article.prototype.itemType = enumItemType.article;
Article.prototype.toggleShowCommandsMode = function(){
	this.showCommandsMode = !(this.showCommandsMode);
}

var targetArticleId = -1;

function manager($scope, $http, $timeout, $window) {
    //, $location, 
    $scope.topicHash = {};
    $scope.portalHash = {};

    $scope.selectedItem;
    $scope.rightItems = [];
    $scope.showAddArticlePanel = false;
    $scope.showAddTopicPanel = false;
    
    $scope.dropMode = false;
    $scope.toggleDropMode = function () {
        $scope.dropMode = !($scope.dropMode);
    };
  
        $scope.portalHash = {};
        $scope.topicHash = {};

        var httpConfig = {headers:{
        	'x-auth-token': sessionStorage.token
        }}
        
        $http.get("/rest/portal", httpConfig).success(function (data) {
            $scope.portals = [];
            for (var index = 0; index < data.length; index++) {
                var serverPortal = data[index];
                var portal = new Portal(serverPortal.id, serverPortal.name);
                if (serverPortal.topics && serverPortal.topics.length > 0) {
                    for (var j = 0; j < serverPortal.topics.length; j++) {
                        var serverTopic = serverPortal.topics[j];
                        var clientTopic = $scope.parseTopic(serverTopic);
                        clientTopic.parent = portal;

                        portal.topics.push(clientTopic);
                        $scope.topicHash[clientTopic.id] = clientTopic;
                    }
                }
                $scope.portals.push(portal);
                $scope.portalHash[portal.id] = portal;
            }
        });

        
        $scope.itemSorted = false;

        $scope.select = function (newSelect) {
            if ($scope.selectedItem && $scope.selectedItem.isSelected) {
                $scope.selectedItem.isSelected = false;

            }
            $scope.selectedItem = newSelect;
            $scope.selectedItem.isSelected = true;
            switch (newSelect.itemType) {
                case enumItemType.topic:
                    $scope.rightItems = newSelect.topics.concat(newSelect.articles);
                    break;
                case enumItemType.portal:
                    // we don't want rightItems to be hooked up with the newSelect.topics
                    // by $scope.rightItems = newSelect.topics;
                    // This will generate different behaviors than the one when selected is topic
                    $scope.rightItems = [].concat(newSelect.topics); 
                    break;
            }
            $scope.rightItems.sort(function (a, b) { return a.displayOrder - b.displayOrder; });



        };
        $scope.parseTopic = function (serverTopic) {
            var clientTopic = new Topic(
                serverTopic.id,
                serverTopic.portalId,
                null,
                serverTopic.name,
                serverTopic.displayOrder
                );

            if (serverTopic.articles && serverTopic.articles.length) {
                for (var articleIndex = 0; articleIndex < serverTopic.articles.length; articleIndex++) {
                    var serverArticle = serverTopic.articles[articleIndex];
                    var clientArticle = new Article(
                        clientTopic,
                        serverArticle.id,
                        serverArticle.name,
                        serverArticle.displayOrder);
                    clientTopic.articles.push(clientArticle);
                }
            }

            if (serverTopic.topics) {
                for (var k = 0; k < serverTopic.topics.length; k++) {
                    var serverChildTopic = serverTopic.topics[k];
                    var childTopic = $scope.parseTopic(serverChildTopic);
                    childTopic.parent = clientTopic;
                    clientTopic.topics.push(childTopic);
                    $scope.topicHash[childTopic.id] = childTopic;
                }
            }

            return clientTopic;
        }
        
        

        $scope.rightSelect = function (newSelect) {
            if (newSelect.itemType == enumItemType.topic) {
                $scope.select(newSelect);
                $scope.expandAllParents(newSelect);
            }
            else if (newSelect.itemType == enumItemType.article) {


   //             var url =  '/article/' + newSelect.id;
                targetArticleId = newSelect.id;
                var url = '/editArticle.html'
                $window.open(url, '_blank');

            }
        };       
        $scope.expandAllParents = function (item) {
            if (item.parent) {
                item.parent.expanded = true;
                $scope.expandAllParents(item.parent);
            }
        };

        $scope.cancelSort = function () {
            $scope.itemSorted = false;
        };
        $scope.saveOrder = function () {
            $scope.rightItems.forEach(function (item, index) {
                item.displayOrder = index;
            });

            var para = $scope.rightItems.map(function (x) {
                return { "id": x.id, "isTopic": x.itemType == enumItemType.topic };
            });

            $http.put('rest/portal/displayOrder', para, httpConfig).success(function (data) {
                alert(data);
            	$scope.itemSorted = false;
                $scope.selectedItem.topics.sort(function (a, b) {
                    return a.displayOrder - b.displayOrder;
                });
            });


        };

        $scope.changeParent = function (parentItem, childItem, index) {
            var parentId = parentItem.id,
                parentType = parentItem.itemType,
                childId = childItem.id,
                childType = childItem.itemType;
            
            var displayOrder = 1;
            if (parentItem.topics && parentItem.topics.length)
            	displayOrder += parentItem.topics.length;
            if (parentItem.articles && parentItem.articles.length)
            	displayOrder += parentItem.articles.length;
            
            // article type cannot be dropped onto portal type, thus their parent must be a topic.
            // This is inforced in the UI directive
            if (childType == enumItemType.article){
            	var paraArticle = {}
            	paraArticle.id = childId;
            	paraArticle.topicId = parentId;
            	paraArticle.displayOrder = displayOrder;
            	
            	 $http.put('rest/article/parent', paraArticle, httpConfig).success(function () {
                     $scope.selectedItem.removeArticle(childItem);
                     parentItem.addArticle(childItem);
                     $scope.rightItems.splice(index, 1);

                 });
            }

            else {
            	var paraTopic = {};
            	paraTopic.id = childId;
            	paraTopic.displayOrder = displayOrder;
            	if (parentType == enumItemType.topic){
            		paraTopic.parentTopicId = parentId;
            		paraTopic.portalId = parentItem.portalId;
            	}
            	else{
            		paraTopic.parentTopicId = 0;
            		paraTopic.portalId = parentId;
            	}
                
                $http.put('rest/topic/parent', paraTopic, httpConfig).success(function () {
                    $scope.selectedItem.removeTopic(childItem);
                    parentItem.addTopic(childItem);
                    $scope.rightItems.splice(index, 1);
                });
            }
        };

        $scope.addArticle = function () {
            if ($scope.selectedItem.itemType != enumItemType.topic)
                return;
            if (!$scope.showAddArticlePanel) {
                $scope.showAddArticlePanel = true;
            }
            
           
        };
        $scope.cancelAddArticle = function () {
            $scope.newArticleTitle = '';
            $scope.showAddArticlePanel = false;
        };
        $scope.saveNewArticle = function (newTitle) {
           
            var display_order = 1;
            if ($scope.rightItems && $scope.rightItems.length)
            	display_order = $scope.rightItems.length + 1;

            var para = {};
            para.name = newTitle;
            para.topicId = $scope.selectedItem.id;
            para.displayOrder = display_order;

            $http.post('/rest/article', para, httpConfig).success(function (data) {
                $scope.newArticleTitle = '';
                $scope.showAddArticlePanel = false;
                
                var articleId = parseInt(data)

                var newArticle = new Article($scope.selectedItem, articleId, newTitle, display_order);
                $scope.selectedItem.articles.push(newArticle);
                $scope.rightItems.push(newArticle);
            });           
        };

        $scope.addTopic = function () {
            if (!$scope.selectedItem) return;
            if (!$scope.showAddTopicPanel) {
                $scope.showAddTopicPanel = true;
            }
        };
        $scope.saveNewTopic = function (newTitle) {
        	 var display_order = 1;
             if ($scope.rightItems && $scope.rightItems.length)
             	display_order = $scope.rightItems.length + 1;
             
            var para = {};
            para.name = newTitle;
            para.displayOrder = display_order;
            
            if ($scope.selectedItem.itemType == enumItemType.portal){
            	para.parentTopicId = 0;
            	para.portalId = $scope.selectedItem.id;
            }
            else{
            	para.parentTopicId = $scope.selectedItem.id;
            	para.portalId = $scope.selectedItem.portalId;
            }
            $http.post('/rest/topic', para, httpConfig).success(function (data) {
                $scope.newTopicTitle = '';
                $scope.showAddTopicPanel = false;
                
                var topicId = parseInt(data);

                var newTopic = new Topic(topicId, para.portalId, $scope.selectedItem, newTitle, display_order);
                $scope.selectedItem.addTopic(newTopic);
                $scope.rightItems.push(newTopic);
                $scope.topicHash[newTopic.id] = newTopic;
            });
        };

        $scope.cancelAddTopic = function () {
            $scope.newTopicTitle = '';
            $scope.showAddTopicPanel = false;
        };

        $scope.originalRightItem;
        $scope.itemToBeEdited;
        $scope.inEditItemMode = false;
        $scope.rename = function (item) {
            $scope.inEditItemMode = true;
            $scope.originalRightItem = item;
            $scope.itemToBeEdited = item.title;
        };
        $scope.cancelRename = function () {
            $scope.inEditItemMode = false;
            $scope.originalRightItem.showCommandsMode = false;
            $scope.itemToBeEdited = '';
            $scope.originalRightItem = {}

        };
        $scope.saveRename = function () {
            var para = {};
           // para.itemType = $scope.originalRightItem.itemType;
           // para.itemId = $scope.originalRightItem.id;
           // para.newTitle = $scope.itemToBeEdited;
            para.id = $scope.originalRightItem.id;
            para.name = $scope.itemToBeEdited;
            
            if ($scope.originalRightItem.itemType == enumItemType.topic){
	            $http.put('rest/topic/title', para, httpConfig).success(function () {
	                $scope.afterTitleChange();
	            });
            }
            else if ($scope.originalRightItem.itemType == enumItemType.article){
            	$http.put('rest/article/title', para, httpConfig).success(function () {
            		$scope.afterTitleChange();
            	});	
            }
            
          };  
          $scope.afterTitleChange = function(){
              $scope.inEditItemMode = false;
              $scope.originalRightItem.title = $scope.itemToBeEdited;
              $scope.originalRightItem.showCommandsMode = false;
              $scope.itemToBeEdited = '';
              $scope.originalRightItem = {};
          
          };
          
}

refBookApp.directive('ngRightItems', function () {
    return function (scope, element, attrs) {

        var toUpdate;
        var startIndex = -1;
        var isDrop = false;

        // watch the model, so we always know what element
        // is at a specific position
        //scope.$watch(attrs.ngSortable, function (value) {
        //    //  toUpdate = value;
        //    toUpdate = scope.rightItems;
        //}, true);

        // use jquery to make the element sortable. This is called
        // when the element is rendered
        
        $(element[0]).sortable({
            items: 'div',
            start: function (event, ui) {
                // on start we define where the item is dragged from
                startIndex = ($(ui.item).index());
            },
            
            stop: function (event, ui) {
                // on stop we determine the new index of the
                // item and store it there
                if (!scope.dropMode) {
                    toUpdate = scope.rightItems;
                    var newIndex = ($(ui.item).index());
                    var toMove = toUpdate[startIndex];
                    toUpdate.splice(startIndex, 1);
                    toUpdate.splice(newIndex, 0, toMove);

                    scope.$apply(function () {

                        scope.itemSorted = true;
                    });
                }
               
            }
        })
    };
});
refBookApp.directive('droppableTopic', function () {
    return function (scope, element, attr) {
        angular.element(element).droppable({
            greedy: true,
            hoverClass: 'dropping',
            accept: '.articleRow, .topicRow',
            
            drop: function (event, ui) {

                
                var index = ui.draggable.index();
                var draggedItem = scope.rightItems[index];
                var targetTopic = scope.topicHash[attr['id']];

                if (draggedItem.itemType == enumItemType.topic && targetTopic.isChildOf(draggedItem)) {
                    console.log('cyclic');
                    return;
                }
                scope.changeParent(targetTopic, draggedItem, index);

            }
        });
        
        scope.$watch('dropMode', function (newValue) {
            if (newValue) {
                angular.element(element).droppable('enable');
            }
            else {
                angular.element(element).droppable('disable');
            }
        });
        
        
    }
});
refBookApp.directive('droppableBook', function () {
    return function (scope, element, attr) {
        angular.element(element).droppable({
            greedy: true,
            hoverClass: 'dropping',
            accept: '.topicRow', // accept topic but not article
            drop: function (event, ui) {
               var index = ui.draggable.index();
               var draggedTopic = scope.rightItems[index];
               
               var targetPortal = scope.portalHash[attr['id']];

               scope.changeParent(targetPortal, draggedTopic, index);


            }
            
        });
        
        scope.$watch('dropMode', function (newValue) {
            if (newValue) {
                angular.element(element).droppable('enable');
            }
            else {
                angular.element(element).droppable('disable');
            }
        });
        
    }
});

refBookApp.directive('ngNewArticle', function ($compile) {
    return function (scope, element, attrs) {
        scope.$watch(attrs.ngNewArticle, function (newValue) {
            if (newValue) {
                
     //           $(element).show()
                $(element).append("<input type='text' placeholder='name here' ng-model='newArticleTitle' /><input type='button' value='ok' ng-click='saveNewArticle(newArticleTitle)' /><input type='button' value='cancel' ng-click='cancelAddArticle()' />");

                $compile(element.contents())(scope);
                element.contents()[0].focus();
                    
                
            }
            else {
                element.empty();
                // $compile(element.contents())(scope);

            }
        });
    };
});
refBookApp.directive('ngNewTopic', function ($compile) {
    return function (scope, element, attrs) {
        scope.$watch(attrs.ngNewTopic, function (newValue) {
            if (newValue) {

                //           $(element).show()
                $(element).append("<input type='text' placeholder='name here' ng-model='newTopicTitle' /><input type='button' value='ok' ng-click='saveNewTopic(newTopicTitle)' /><input type='button' value='cancel' ng-click='cancelAddTopic()' />");

                $compile(element.contents())(scope);
                element.contents()[0].focus();


            }
            else {
                element.empty();
                // $compile(element.contents())(scope);

            }
        });
    };
});

