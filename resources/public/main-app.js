/**
 * Created by macquest on 6/21/14.
 */

var app = angular.module('chatApp', []);


var AnimDuration = 4000;

var transContent = function  (domElmt) {
	// body...
	var content = document.getElementById(domElmt);
	Jacked.fadeIn(content, {duration: AnimDuration});
};

app.run(['$rootScope', function ($rootScope) {
	$rootScope.myWebSocket = new WebSocket("ws://localhost:3000/ws");
}])

app.directive('zensoal', [function () {
	return {

		templateUrl: '/directive-templates/zensoal.html',
		replace: false,
		transclude: true,
		restrict: 'E',
		scope: {},
		controller: function ($scope, $element, $attrs, $transclude, $rootScope) {
			$scope.answerSoal = function ( answer ) {
				var packageJawaban = {
					user : user,
					chatroom : chatroom,
					dataType : "answer",
					message : answer
				}
				$rootScope.myWebSocket.send(JSON.stringify(packageJawaban));
				console.log(packageJawaban);
			}
		},
		compile: function compile(tElement, tAttrs, transclude) {
			return function postLink(scope, iElement, iAttrs, controller) {

			}
		},
		link: function postLink(scope, elmt, attrs) {

		}
	};
}])


app.controller('MainCtrl', ['$scope', '$rootScope', function ($scope, $rootScope) {
	$scope.messages = [];


	$rootScope.myWebSocket.onopen = function(evt) {
		console.log(evt);
	};

	$rootScope.myWebSocket.onmessage = function(evt) {
		var dataPackage = JSON.parse(evt.data);

		if (dataPackage.type == "new-user") {

			$scope.users = [];

			for (var i = 0; i < dataPackage.list.length; i++) {
				$scope.users.push(dataPackage.list[i]);
				console.log(dataPackage.list[i], $scope.users[i]);
			}

		}
		if ((dataPackage.type == "message") || (dataPackage.type == "soal")) {
			$scope.messages.unshift(dataPackage);
			if ($scope.messages.length > 50) {
				$scope.messages.pop();
			}

		}

		$scope.$digest();
		transContent('message0');
		console.log(evt);
	};

	$scope.sendChat = function ( message ) {
		if (message.length != 0) {
			var data = {
				user: user,
				chatroom: chatroom,
				message: message,
				dataType : "message"
			};
			$rootScope.myWebSocket.send(JSON.stringify(data));
			$scope.message = "";
		}

	}
}])
