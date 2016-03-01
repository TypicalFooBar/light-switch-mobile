angular.module('light-switch-mobile.controllers')

.controller('EditController', function($scope, $rootScope, $http, $q, $ionicHistory, $ionicNavBarDelegate, $ionicLoading, $localStorage) {
    $scope.lightSwitchList = null;
    $scope.lightSwitchOriginalNames = [];
    
    $scope.init = function() {
        $ionicNavBarDelegate.showBackButton(true);
        
        $scope.getLightSwitchList();
    };
    
    $scope.getLightSwitchList = function() {
        $http.get($rootScope.lightSwitchUrl + "/api/light-switch?action=getLightSwitchList")
        .then(function success(response) {
            $scope.lightSwitchList = response.data;
            
            // Set the original names
            angular.forEach($scope.lightSwitchList, function(lightSwitch, key) {
                $scope.lightSwitchOriginalNames.push(lightSwitch.name);
            });
        }, function error(response) {
            
        });
    };
    
    $scope.updateLightSwitches = function() {
        // Show a loading overlay
        $ionicLoading.show({
            template: 'Updating...'
        });
        
        var httpCalls = [];
        
        for (var i = 0; i < $scope.lightSwitchList.length; ++i) {
            if ($scope.lightSwitchList[i].name != $scope.lightSwitchOriginalNames[i]) {
                var httpCall = $http({
                    url: $rootScope.lightSwitchUrl + "/api/light-switch?action=updateLightSwitch",
                    method: "GET",
                    params: {
                        lightSwitch: angular.toJson($scope.lightSwitchList[i])
                    }
                })
                .then(function success(response) {
                    
                }, function error(response) {
                    
                });
                
                httpCalls.push(httpCall);
            }
        };
        
        // Once all http calls have completed
        $q.all(httpCalls).then(function() {
            $ionicHistory.goBack();
            $ionicLoading.hide();
        });
    };
    
    $scope.init();
});