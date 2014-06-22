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


app.controller('MainCtrl', ['$scope', '$http', function ($scope, $http) {
	$scope.messages = [];


	var myWebSocket = new WebSocket("ws://localhost:3000/ws");

	myWebSocket.onopen = function(evt) {

		console.log(evt);
	};

	myWebSocket.onmessage = function(evt) {
		var dataPackage = JSON.parse(evt.data);

		if (dataPackage.type == "new-user") {

			$scope.users = [];

			for (var i = 0; i < dataPackage.list.length; i++) {
				$scope.users.push(dataPackage.list[i]);
				console.log(dataPackage.list[i], $scope.users[i]);
			}

		}
		if (dataPackage.type == "message") {
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
		var data = {
			user : user,
			message : message
		};
		myWebSocket.send(JSON.stringify(data));
		$scope.message = "";

	}
}])