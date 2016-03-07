angular.module('light-switch-mobile.controllers')

.controller('HomeController', function($scope, $rootScope, $http, $ionicLoading, $ionicPlatform) {
    $scope.lightSwitchList = null;
    $scope.loadingDataFromLightSwitchServer = false;
    $scope.loadFromServerFailed = false;
    
    $scope.init = function() {
        // Show a loading overlay
        $ionicLoading.show({
            template: 'Loading...'
        });
        
        $scope.getLightSwitchList();
    };
    
    $scope.getLightSwitchList = function() {
        $scope.loadingDataFromLightSwitchServer = true;
        
        $http.get($rootScope.lightSwitchServer.url() + "/api/light-switch?action=getLightSwitchList")
        .then(function success(response) {
            // Set the light switch list
            $scope.lightSwitchList = response.data;
            
            // Stop showing the loading overlay
            $ionicLoading.hide();
            
            // We're no longer loading data from the server
            $scope.loadingDataFromLightSwitchServer = false;
            
            // Load was successful
            $scope.loadFromServerFailed = false;
        }, function error(response) {
            // We're no longer loading data from the server
            $scope.loadingDataFromLightSwitchServer = false;
            
            // Stop showing the loading overlay
            $ionicLoading.hide();
            
            // Load failed
            $scope.loadFromServerFailed = true;
        });
    };
    
    $scope.lightSwitchClicked = function(lightSwitch) {
        // Toggle the light switch
        lightSwitch.active = !lightSwitch.active;
        
        // Stringify the light switch
        var lightSwitchJsonString = angular.toJson(lightSwitch);
        
        // Update the server
        $http({
            url: $rootScope.lightSwitchServer.url() + "/api/light-switch?action=updateLightSwitch",
            method: "GET",
            params: {
                lightSwitch: lightSwitchJsonString
            }
        })
        .then(function success(response) {
            var i = 0;
        }, function error(response) {
            var i = 0;
        });
    };
    
    $ionicPlatform.on('resume', function(){
        $scope.init();
    });
    
    $scope.init();
});