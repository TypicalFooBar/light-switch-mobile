angular.module('light-switch-mobile.controllers')

.controller('HomeController', function($scope, $rootScope, $http, $localStorage) {
    $scope.lightSwitchList = null;
    
    $scope.init = function() {
        $scope.getLightSwitchList();
    };
    
    $scope.getLightSwitchList = function() {
        $http.get($rootScope.lightSwitchUrl + "/api/light-switch?action=getLightSwitchList")
        .then(function success(response) {
            $scope.lightSwitchList = response.data;
        }, function error(response) {
            
        });
    };
    
    $scope.lightSwitchClicked = function(lightSwitch) {
        // Toggle the light switch
        lightSwitch.active = !lightSwitch;
        
        // Stringify the light switch
        var lightSwitchJsonString = angular.toJson(lightSwitch);
        
        // Update the server
        $http({
            url: $rootScope.lightSwitchUrl + "/api/light-switch?action=updateLightSwitch",
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
    
    $scope.init();
});