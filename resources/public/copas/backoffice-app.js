/**
 * Created by macquest on 6/11/14.
 */

var app = angular.module('backOfficeApp', []);

app.controller('MainCtrl', ['$scope', '$http', function ($scope, $http) {
	$scope.postForm = function(form) {
		$http.post(baseURL + "/post-form", form)
			.success(function (data) {
				$scope.data = data.data;
				console.log(data.data);
				alert("sukses");
			})
			.error(function (err) {
				alert("error");
			})
	}
}])

app.controller('addNotesGroupCtrl', ['$scope', '$http', function ($scope, $http) {
	$scope.checkCGID = function (cgid) {
		$http.get(baseURL + "/notes/checkcgid/" + cgid)
			.success(function (data) {
				$scope.cgidMessage = data.message;
			})
			.error(function (err) {
				alert("Official zenius statement: Aduh maap internet lagi tolol! \nActual error: " + err);
			})
	};

	$scope.postForm = function(form) {

		$http.put(baseURL + "/notes/act-add-notes-group", form)
			.success(function (data) {
			   $scope.message = data.message;
				alert("sukses");
				$scope.form = {};
			})
			.error(function (err) {
				alert("error" + err);
			})
	}
}])

app.controller('LoginCtrl', ['$scope', '$http', function ($scope, $http) {
	$scope.loggedIn = false;
	$scope.postForm = function(form) {
		$http.put(baseURL + "/act-login", form)
			.success(function (data) {
			   if ( data.status ) {
			      window.location.replace(baseURL + "/home");
			   } else {
					$scope.loggedIn = true;
					$scope.message = data.message;
					transContent('formset');
					angular.element('formName').trigger('focus');
			   };
			})
			.error(function (err) {

			})
	}
}])


























