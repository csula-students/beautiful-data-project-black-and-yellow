<!DOCTYPE html>
<html ng-app="datascience">
	<head>
		<title>CS454: Datascience | Black-And-Yellow</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"/>
		<style>
		</style>
	</head>
	<body>
		<nav class="navbar navbar-default" ng-controller="SearchController">
			<div class="container-fluid">
				<div class="navbar-header">
					<a href="/" class="navbar-brand">Black And Yellow</a>
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input type="text" class="form-control" placeholder="AAPL" ng-change="searchData()" ng-model="query.value" ng-disabled="!query.enabled"/>
						</div>
					</form>
				</div>
			</div>
		</nav>
		
		<div class="container-fluid">
			<div class="col-lg-12" ng-controller="GraphingCtrl"></div>
		</div>
		
		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.9/angular.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.9/angular-sanitize.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.min.js"></script>
		<script>
		(function(angular,jQuery){
			angular.module("datascience",["ngSanitize"])
			.service("SearchService",["$http",function($http){
				this.get = function(query,callback) {
					var _query = "";
					
					for(param in query){
						_query = param+"="+query[param]+"&";
					}
					
					_query = _query.substr(0,_query.length-1);
					$http.get("/ajax.php?"+_query)
					.then(function(response){
						callback(response.data);
					},function(response){
						console.log(response);
					});
				};
			}])
			.controller("SearchController",["$scope","$rootScope","$timeout","SearchService",function($scope,$rootScope,$timeout,SearchService){
				var promise = null;
				$scope.query = {
					value: "",
					enabled: true
				};
				$scope.searchData = function() {
					if(promise != null) {
						$timeout.cancel(promise);
					}
					promise = $timeout(function(){
						var query = {
							"stock": $scope.query.value
						}
						console.log(SearchService);
						SearchService.get(query,function(data){
							$rootScope.$broadcast("event",data);
						});
					},500);
				};
			}])
			.controller("GraphingCtrl",["$scope",function($scope){
				$scope.graphData = {};
				$scope.$on("event",function(event,data){
					if(data && typeof(data) == "object") {
						$scope.graphData = data;
					}
				});
			}]);
		})(angular,jQuery);
		</script>
	</body>
</html>