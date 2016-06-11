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
							<label for="stock">Stock:</label>
							<input type="text" id="stock" class="form-control" placeholder="AAPL" ng-model="stock.value"/>
						</div>
						<div class="form-group">
							<label for="start-date">Start Date:</label>
							<div class="input-group">
								<input type="text" class="form-control" id="start-date" uib-datepicker-popup="{{format}}" ng-model="startDate.value" is-open="startDate.opened" datepicker-options="dateOptions" ng-required="true" close-text="Close" alt-input-formats="altInputFormats" />
								<span class="input-group-btn">
									<button type="button" class="btn btn-default" ng-click="open(1)"><i class="glyphicon glyphicon-calendar"></i></button>
								</span>
							</div>
						</div>
						<div class="form-group">
							<label for="end-date">End Date:</label>
							<div class="input-group">
								<input type="text" class="form-control" id="end-date" uib-datepicker-popup="{{format}}" ng-model="endDate.value" is-open="endDate.opened" datepicker-options="dateOptions" ng-required="true" close-text="Close" alt-input-formats="altInputFormats" />
								<span class="input-group-btn">
									<button type="button" class="btn btn-default" ng-click="open(2)"><i class="glyphicon glyphicon-calendar"></i></button>
								</span>
							</div>
						</div>
						<div class="form-group">
							<label for="limit"># of Points</label>
							<input type="number" min="{{points.min}}" max="{{points.max}}" id="limit" step="10" ng-model="points.value" />
						</div>
						<div class="form-group">
							<button ng-click="searchData()" class="btn btn-primary">Search</button>
						</div>
					</form>
				</div>
			</div>
		</nav>
		
		<div class="container-fluid">
			<div class="col-lg-12" ng-controller="GraphingCtrl">
				<canvas id="{{model.id}}" width="400" height="400"></canvas>
			</div>
		</div>
		
		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.9/angular.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.9/angular-sanitize.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.13.0/moment.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.13.0/locale/en-ca.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.min.js"></script>
		<script src="/js/ui-bootstrap-tpls-1.3.3.min.js"></script>
		<script>
		(function(angular,jQuery){
			angular.module("datascience",["ngSanitize",'ui.bootstrap'])
			.service("SearchService",["$http",function($http){
				this.get = function(query,callback) {
					var _query = "";
					
					for(param in query){
						_query += param+"="+query[param]+"&";
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
				$scope.format = "MMMM dd, yyyy";
				$scope.stock = {
					value: "",					
					enabled: true
				};
				$scope.startDate= {
					value: new Date(),
					opened: false
				};
				$scope.endDate= {
					value: new Date(),
					opened: false
				};
				$scope.points = {
					value: 10,
					max:10000,
					min:10
				};
				$scope.dateOptions = {
					    dateDisabled: false,
					    formatYear: 'yy',
					    maxDate: new Date(),
					    minDate: new Date(2012,01,01),
					    startingDay: 1
					  };
				$scope.open = function(num) {
					switch(num){
						case 1:
							$scope.startDate.opened = true;
							$scope.endDate.opened = false;
							break;
						case 2:
							$scope.startDate.opened = false;
							$scope.endDate.opened = true;
							break;
						default:
							$scope.startDate.opened = false;
							$scope.endDate.opened = false;
							break;
					};
				};
				$scope.searchData = function() {
					if(promise != null) {
						$timeout.cancel(promise);
					}
					promise = $timeout(function(){
						if($scope.stock.value.length == 0) {
							return;
						}
						$scope.points.value = parseInt($scope.points.value);
						$scope.points.value = $scope.points.value < $scope.points.min ? $scope.points.min : ($scope.points.value > $scope.points.max ? $scope.points.max : $scope.points.value);

						var query = {
							"stock": $scope.stock.value,
							"start": parseInt($scope.startDate.value.getTime() / 1000),
							"end":parseInt($scope.endDate.value.getTime() / 1000),
							"size":$scope.points.value
						}
						
						SearchService.get(query,function(data){
							$rootScope.$broadcast("newData",{stock: $scope.stock.value, values: data});
						});
					},500);
				};
			}])
			.controller("GraphingCtrl",["$scope",function($scope){
				$scope.model = {
						id: "graph",
						stock: '',
						data: {
							stocks:[],
							tweets:[]
						},
						chartInstance: null
				};
				var getStockValues = function() {
					var list = [];
					for(i in $scope.model.data.stocks) {
						list.push({
							y:$scope.model.data.stocks[i].open,
							x:new Date($scope.model.data.stocks[i].date *1000)
						});
					}					
					return list.sort(function(a,b){
						return a.x-b.x;
					});
				};
				var getStockTimestamp = function() {
					var list = [];
					for(i in $scope.model.data.stocks) {
						list.push(new Date($scope.model.data.stocks[i].date *1000));
					}
					return list;
				}
				
				var updateChart = function() {
					if($scope.model.chartInstance == null) {
						$scope.model.chartInstance = new Chart(document.getElementById($scope.model.id), {
							type: 'line',
							data: {
								labels: [getStockTimestamp()],
								datasets: [{
									label: 'Stock Open Values',
									data: getStockValues(),
									backgroundColor: 'rgba(255, 99, 132, 0.2)',
									borderColor: 'rgba(255,99,132,1)',
									borderWidth: 1
								}]
							},
						    options: {
						        scales: {
						            yAxes: [{
						                ticks: {
						                    beginAtZero:true
						                }
						            }],
						            xAxes: [{
						                type: 'time',
						                position: 'bottom',
						                time: {
						                    displayFormats: {
						                        quarter: 'MMM YYYY'
						                    }
						                }
						            }]
						        },
						        responsive: true
						    }							
						});
					} else {
						$scope.model.chartInstance.data.labels = getStockTimestamp();
						$scope.model.chartInstance.data.datasets[0].data = getStockValues();
						$scope.model.chartInstance.update();
					}
					
				};

				var init = function() {
					jQuery(document).ready(function(){
						jQuery("#"+$scope.model.id).attr({
							"width":window.innerWidth,
							"height":window.innerHeight - jQuery("nav").outerHeight()
						});
					});
				};
				
				$scope.$on("newData",function(event,data){
					if(data && typeof(data) == "object") {
						$scope.model.stock = data.stock; 
						
						if(typeof(data.values.stocks) != "undefined") {
							$scope.model.data.stocks = data.values.stocks;
						}

						if(typeof(data.values.tweets) != "undefined") {
							$scope.model.data.tweets = data.values.tweets;
						}

						updateChart();
					}
				});

				init();
			}]);
		})(angular,jQuery);
		</script>
	</body>
</html>