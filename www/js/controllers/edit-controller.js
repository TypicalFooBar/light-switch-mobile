angular.module('light-switch-mobile.controllers')

.controller('EditController', function($scope, $rootScope, $http, $q, $ionicHistory, $ionicNavBarDelegate, $ionicLoading, $localStorage, $ionicPopup) {
    $scope.lightSwitchList = null;
    $scope.lightSwitchOriginalNames = [];
    
    $scope.lightSwitchServer = {
        protocol: $localStorage.get($rootScope.localStorageKeys.lightSwitchServer.protocol, 'http'),
        address: $localStorage.get($rootScope.localStorageKeys.lightSwitchServer.address, '192.168.1.116'),
        port: $localStorage.get($rootScope.localStorageKeys.lightSwitchServer.port, '80')
    };
    
    $scope.lightSwitchService = {
        wifiName: $localStorage.get($rootScope.localStorageKeys.lightSwitchService.wifiName, ''),
        useWelcomeHomeLights: false,
        lightSwitchIdList: $localStorage.get($rootScope.localStorageKeys.lightSwitchService.lightSwitchIdList, [])
    }
    
    $scope.init = function() {
        $ionicNavBarDelegate.showBackButton(true);
        
        $scope.getLightSwitchList();
        
        // If it's a comma-delimited string
        if ($scope.lightSwitchService.lightSwitchIdList.indexOf(',') > -1) {
            // Turn it into an array of integers
            $scope.lightSwitchService.lightSwitchIdList = $scope.lightSwitchService.lightSwitchIdList.split(',').map(Number);
        }
        
    };
    
    $scope.toggleLightSwitchIdList = function(id) {
        // Find the index of the ID
        var index = $scope.lightSwitchService.lightSwitchIdList.indexOf(id)
        
        // If the ID is not in the list
        if (index == -1) {
            // Add the ID to the list
            $scope.lightSwitchService.lightSwitchIdList.push(id);
        }
        else { // Else, if the ID is in the list
            // Remove the ID from the list
            $scope.lightSwitchService.lightSwitchIdList.splice(index, 1);
        }
    };
    
    $scope.getLightSwitchList = function() {
        $http.get($rootScope.lightSwitchServer.url() + "/api/light-switch?action=getLightSwitchList")
        .then(function success(response) {
            $scope.lightSwitchList = response.data;
            
            angular.forEach($scope.lightSwitchList, function(lightSwitch, key) {
                // Save a copy of the original names
                $scope.lightSwitchOriginalNames.push(lightSwitch.name);
                
                // Add a variable to the light switch object to track if it is used with the service.
                // If the light switch's ID is already in the list of light switches to use.
                if ($scope.lightSwitchService.lightSwitchIdList.indexOf(lightSwitch.id) > -1) {
                    // Set it to true
                    lightSwitch.useWithService = true;
                }
                else {
                    // Else, set it to false
                    lightSwitch.useWithService = false;
                }
            });
        }, function error(response) {
            
        });
    };
    
    $scope.save = function(goBackAfterSave, finishedSavingCallback) {
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
        
        // Update the Service Settings
        $localStorage.set($rootScope.localStorageKeys.lightSwitchService.wifiName, $scope.lightSwitchService.wifiName);
        $localStorage.set($rootScope.localStorageKeys.lightSwitchService.lightSwitchIdList, $scope.lightSwitchService.lightSwitchIdList);
        
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
            if (goBackAfterSave) {
                $ionicHistory.goBack();
            }
            $ionicLoading.hide();
            
            finishedSavingCallback();
        });
    };
    
    $scope.toggleWelcomeHomeLights = function() {
        if ($scope.lightSwitchService.useWelcomeHomeLights == true) {
            // Save before starting the service, but do not go back to the home page
            $scope.save(false, function() {
                $scope.startService();
            });
        }
        else {
            $scope.stopService();
        }
    }
    
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
            },
            $scope.lightSwitchService.wifiName,
            $rootScope.lightSwitchServer.url(),
            $scope.lightSwitchService.lightSwitchIdList);
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