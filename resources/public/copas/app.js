var app = angular.module('zeduApp', ['ui.router', 'angles']);

var AnimDuration = 2000;

var transContent = function  (domElmt) {
    // body...
    var content = document.getElementById(domElmt);
    Jacked.fadeIn(content, {duration: AnimDuration});
};

var openModal = function (domModalId, modalUrl) {
	// body...
	$(domModalId).foundation('reveal', 'open', modalUrl);

}



app.config(function($stateProvider, $urlRouterProvider) {
  //
  // For any unmatched url, redirect to /state1
  $urlRouterProvider.otherwise('/home');
  //
  // Now set up the states
  $stateProvider
    .state('home', {
      url: '/home',
      templateUrl: "/main-page/home.html",
      controller: "HomeCtrl"
    })
    .state('home.chapter01', {
      url: "/home.chapter01",
      templateUrl: '/partial-home/exec-summary.html',
      controller: "HomeCtrl"
    })
    .state('home.chapter02', {
    	url: "/home.chapter02",
    	templateUrl: "/partial-home/business-model.html",
    	controller: "HomeCtrl"
    })
    .state('home.chapter03', {
    	url: '/home.chapter03',
    	templateUrl: '/partial-home/business-units.html',
    	controller: 'HomeCtrl'
    })
    .state('home.chapter04', {
    	url: '/home.chapter04',
    	templateUrl: '/partial-home/improvement-students.html',
    	controller: 'HomeCtrl'
    })
    .state('home.chapter05', {
    	url: '/home.chapter05',
    	templateUrl: '/partial-home/improvement-parents.html',
    	controller: 'HomeCtrl'
    })
    .state('home.chapter06', {
    	url: '/home.chapter06',
    	templateUrl: '/partial-home/improvement-units.html',
    	controller: 'HomeCtrl'
    })
    .state('home.chapter07', {
    	url: '/home.chapter07',
    	templateUrl: '/partial-home/proposed-funding.html',
    	controller: 'HomeCtrl'
    })
    .state('home.chapter08', {
    	url: '/home.chapter08',
    	templateUrl: '/partial-home/industry-landscape.html',
    	controller: 'HomeCtrl'
    })
    .state('about', {
    	url: '/about',
    	templateUrl: '/main-page/about.html',
    	controller: 'AboutCtrl'
    })
    .state('team', {
    	url: '/team',
    	templateUrl: '/main-page/team.html',
    	controller: 'TeamCtrl'
    })
    .state('demo', {
    	url: '/demo',
    	templateUrl: '/main-page/demo.html',
    	controller: 'DemoCtrl'
    })
    .state('demo.page1', {
    	url: '/demo.page1',
    	templateUrl: '/partial-demo/existing.html',
    	controller: 'DemoCtrl'
    })
    .state('demo.page2', {
    	url: '/demo.page2',
    	templateUrl: '/partial-demo/notes.html',
    	controller: 'DemoCtrl'
    })
    .state('demo.page3', {
    	url: '/demo.page3',
    	templateUrl: '/partial-demo/notes-anim.html',
    	controller: 'DemoCtrl'
    })
    .state('demo.page4', {
    	url: '/demo.page4',
    	templateUrl: '/partial-demo/quiz.html',
    	controller: 'DemoCtrl'
    })
	.state('demo.page5', {
		url: '/demo.page5',
		templateUrl: '/partial-demo/modules.html',
		controller: 'DemoCtrl'
	})    
});


app.run(function ($rootScope) {
    $rootScope.$on('$viewContentLoaded', function () {
        $(document).foundation();
    });
});


app.controller('MainCtrl', ['$scope', 
	function ($scope) {
		$scope.message = "Business development plan & funding proposal";
}])

app.controller('HomeCtrl', ['$scope', 
	function ($scope) {
		transContent("dom-main-content");
		$scope.trafficByTypeChart = [
			{ 
				value: 46,
				color: "CornFlowerBlue"
			},
			{
				value: 31,
				color: "Chartreuse"
			},
			{
				value: 15,
				color: "DarkOrange"
			},
			{
				value: 4,
				color: "Magenta"
			},
			{
				value: 3,
				color: "Cyan"
			},
			{
				value: 1,
				color: "Yellow"
			}
		];

		$scope.visitorsGrowthChart = {
		    labels : ["2012/2013","2013/2014","2014/2015", "2015/2016", "2016/2017", "2017/2018", "2018/2019"],
		    datasets : [
		        {
		            fillColor : "Magenta",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [1900, 4200, 7376, 11620, 16365, 22853, 29890]
		        }

		        
		    ], 
		};

		$scope.prospectsGrowthChart = {
		    labels : ["2013/2014","2014/2015", "2015/2016", "2016/2017", "2017/2018", "2018/2019"],
		    datasets : [
		        {
		            fillColor : "Cyan",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [420, 681, 798, 1403, 2137, 3160]
		        }

		        
		    ], 
		};

		$scope.subscribersGrowthChart = {
		    labels : ["2013/2014","2014/2015", "2015/2016", "2016/2017", "2017/2018", "2018/2019"],
		    datasets : [
		        {
		            fillColor : "Yellow",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [7000, 47600, 149000, 295826, 454006, 670697]
		        }
		        
		    ], 
		};

		$scope.revenueChart = {
		    labels : ["2008/2009", "2009/2010", "2010/2011", "2011/2012", "2012/2013", "*2013/2014"],
		    datasets : [
		        {
		            fillColor : "#ff3498",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [1381, 3720, 3943, 3320, 4313, 5760]
		        }

		        
		    ], 
		};

		$scope.znetRevenueChart = {
		    labels : ["2010/2011", "2011/2012", "2012/2013", "*2013/2014"],
		    datasets : [
		        {
		            fillColor : "Yellow",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [48, 199, 874, 1789]
		        }

		        
		    ], 
		};

		$scope.profitChart = {
		    labels : ["2008/2009", "2009/2010", "2010/2011", "2011/2012", "2012/2013", "2013/2014"],
		    datasets : [
		        {
		            fillColor : "SteelBlue",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [65, 224, -8, -221, -510, 456]
		        },
		        
		    ], 
		};
}])

app.controller('Chapter01Ctrl', ['$scope', 
	function ($scope) {
		transContent('dom-main-content');
		$(document).foundation();
}])

app.controller('DemoCtrl', ['$scope', 
	function ($scope) {
		transContent('dom-main-content');
	
}])

app.controller('AboutCtrl', ['$scope', 
	function ($scope) {
		transContent('dom-about-content');
		$scope.revenueChart = {
		    labels : ["2008/2009", "2009/2010", "2010/2011", "2011/2012", "2012/2013", "*2013/2014"],
		    datasets : [
		        {
		            fillColor : "#ff3498",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [1381, 3720, 3943, 3320, 4313, 5760]
		        }

		        
		    ], 
		};

		$scope.znetRevenueChart = {
		    labels : ["2010/2011", "2011/2012", "2012/2013", "*2013/2014"],
		    datasets : [
		        {
		            fillColor : "Yellow",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [48, 199, 874, 1789]
		        }

		        
		    ], 
		};

		$scope.profitChart = {
		    labels : ["2008/2009", "2009/2010", "2010/2011", "2011/2012", "2012/2013", "2013/2014"],
		    datasets : [
		        {
		            fillColor : "SteelBlue",
		            strokeColor : "#e67e22",
		            pointColor : "rgba(151,187,205,0)",
		            pointStrokeColor : "#e67e22",
		            data : [65, 224, -8, -221, -510, 456]
		        },
		        
		    ], 
		};
	
}])

app.controller('TeamCtrl', ['$scope', 
	function ($scope) {
		transContent('dom-team-content');
}])






























