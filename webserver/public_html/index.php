<!DOCTYPE html>
<html ng-app="datascience">
	<head>
		<title>CS454: Datascience | Black-And-Yellow</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<style src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"></style>
		<style>
		</style>
	</head>
	<body>
		<nav class="navbar navbar-default" ng-controller="SearchController">
			<div class="container-fluid">
				<div class="navbar-header">
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input type="text" class="form-control" placeholder="AAPL" ng-change="searchData" ng-model="query.value" ng-disabled="!query.enabled"/>
						</div>
					</form>
				</div>
			</div>
		</nav>
		
		<div class="container-fluid">
			<div class="col-lg-12" ng-controller="GraphingCtrl"></div>
		</div>
		
		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.9/angular.min.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.9/angular-sanitize.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.min.js"></script>
		<script>
		(function(angular,jQuery){
			angular.module("datascience",["ngSanitize"])
			.service("SearchService",["$http",function($http){
				this.get = function(stock,callback) {
					$http.get("/ajax.php?query="+stock)
					.done(function(){
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
						SearchService.get($scope.query.value,function(data){
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