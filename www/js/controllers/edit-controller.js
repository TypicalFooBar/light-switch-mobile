angular.module('light-switch-mobile.controllers')

.controller('EditController', function($scope, $rootScope, $http, $q, $ionicHistory, $ionicNavBarDelegate, $ionicLoading, $localStorage, $ionicPopup) {
    $scope.lightSwitchList = null;
    $scope.lightSwitchOriginalNames = [];
    
    // Client settings start out with default values
    $scope.clientSettings = {
        server: {
            protocol: 'http',
            address: '192.168.1.116',
            port: 80
        },
        welcomeHomeLights: {
            active: false,
            wifiName: '',
            lightSwitchIdList: [],
            timeOfDay: {
                start: new Date(93600000),
                end: new Date(50400000)
            },
            minWifiDisconnectMinutes: 10
        }
    };
    
    $scope.init = function() {
        $ionicNavBarDelegate.showBackButton(true);
        
        $scope.getLightSwitchList();
        
        // Get the client settings json string
        var clientSettingsJsonString = $localStorage.get('ClientSettings', null);
        
        // If not null
        if (clientSettingsJsonString != null) {
            // Set the client settings object to the values from the localstorage database
            $scope.clientSettings = angular.fromJson(clientSettingsJsonString);
            
            // Convert the start and end strings to dates
            $scope.clientSettings.welcomeHomeLights.timeOfDay.start = new Date($scope.clientSettings.welcomeHomeLights.timeOfDay.start);
            $scope.clientSettings.welcomeHomeLights.timeOfDay.end = new Date($scope.clientSettings.welcomeHomeLights.timeOfDay.end);
        }
    };
    
    $scope.toggleLightSwitchIdList = function(id) {
        // Find the index of the ID
        var index = $scope.clientSettings.welcomeHomeLights.lightSwitchIdList.indexOf(id)
        
        // If the ID is not in the list
        if (index == -1) {
            // Add the ID to the list
            $scope.clientSettings.welcomeHomeLights.lightSwitchIdList.push(id);
        }
        else { // Else, if the ID is in the list
            // Remove the ID from the list
            $scope.clientSettings.welcomeHomeLights.lightSwitchIdList.splice(index, 1);
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
                if ($scope.clientSettings.welcomeHomeLights.lightSwitchIdList.indexOf(lightSwitch.id) > -1) {
                    // Set it to true
                    lightSwitch.useAsWelcomeHomeLight = true;
                }
                else {
                    // Else, set it to false
                    lightSwitch.useAsWelcomeHomeLight = false;
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
        
        // Save the client settings
        // Change toJSON() to use toString() for the dates, otherwise it defaults to toISOString().
        $scope.clientSettings.welcomeHomeLights.timeOfDay.start.toJSON = function() { return this.toString(); }
        $scope.clientSettings.welcomeHomeLights.timeOfDay.end.toJSON = function() { return this.toString(); }
        $localStorage.set('ClientSettings', angular.toJson($scope.clientSettings));
        
        // List to store the http calls, so we know when they all have finished
        var httpCalls = [];
        
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
            
            if (finishedSavingCallback != null) {
                finishedSavingCallback();
            }
        });
    };
    
    $scope.init();
});