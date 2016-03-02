angular.module('light-switch-mobile.controllers')

.controller('EditController', function($scope, $rootScope, $http, $q, $ionicHistory, $ionicNavBarDelegate, $ionicLoading, $localStorage, $ionicPopup) {
    $scope.lightSwitchList = null;
    $scope.lightSwitchOriginalNames = [];
    
    $scope.lightSwitchServer = {
        protocol: $localStorage.get($rootScope.localStorageKeys.lightSwitchServer.protocol, 'http'),
        address: $localStorage.get($rootScope.localStorageKeys.lightSwitchServer.address, '192.168.1.116'),
        port: $localStorage.get($rootScope.localStorageKeys.lightSwitchServer.port, '80')
    }
    
    $scope.init = function() {
        $ionicNavBarDelegate.showBackButton(true);
        
        $scope.getLightSwitchList();
    };
    
    $scope.getLightSwitchList = function() {
        $http.get($rootScope.lightSwitchServer.url() + "/api/light-switch?action=getLightSwitchList")
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
            template: 'Saving...'
        });
        
        // List to store the http calls, so we know when they all have finished
        var httpCalls = [];
        
        // Update the Server Settings
        $localStorage.set($rootScope.localStorageKeys.lightSwitchServer.protocol, $scope.lightSwitchServer.protocol);
        $localStorage.set($rootScope.localStorageKeys.lightSwitchServer.address, $scope.lightSwitchServer.address);
        $localStorage.set($rootScope.localStorageKeys.lightSwitchServer.port, $scope.lightSwitchServer.port);
        
        // Make sure the light switch list is NOT null
        if ($scope.lightSwitchList != null) {
            for (var i = 0; i < $scope.lightSwitchList.length; ++i) {
                if ($scope.lightSwitchList[i].name != $scope.lightSwitchOriginalNames[i]) {
                    var httpCall = $http({
                        url: $rootScope.lightSwitchServer.url() + "/api/light-switch?action=updateLightSwitch",
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
        }
        
        // Once all http calls have completed
        $q.all(httpCalls).then(function() {
            $ionicHistory.goBack();
            $ionicLoading.hide();
        });
    };
    
    $scope.startService = function() {
        window.LightSwitchServicePlugin.startService(
            function(response) { // Success
                $ionicPopup.alert({
                    title: 'Service Response',
                    template: response.message
                });
            },
            function(response) { // Error
                $ionicPopup.alert({
                    title: 'Service Response',
                    template: response.message
                });
            });
    };
    
    $scope.stopService = function() {
        window.LightSwitchServicePlugin.stopService(
            function(response) { // Success
                $ionicPopup.alert({
                    title: 'Service Response',
                    template: response.message
                });
            },
            function(response) { // Error
                $ionicPopup.alert({
                    title: 'Service Response',
                    template: response.message
                });
            });
    };
    
    $scope.init();
});