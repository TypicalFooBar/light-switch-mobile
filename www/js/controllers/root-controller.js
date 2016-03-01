angular.module('light-switch-mobile.controllers', [])

// Add the global variables to the rootScope
.run(function ($rootScope, $window) {
    // Set root-scope variables
    $rootScope.localStorageKeys = {
        lightSwitchServer: {
            protocol: 'lightSwitchServerProtocol',
            address: 'lightSwitchServerAddress',
            port: 'lightSwitchServerPort'
        }
    };
    
    $rootScope.lightSwitchServer = {
        url: function() {
            var url =
                ($window.localStorage[$rootScope.localStorageKeys.lightSwitchServer.protocol] || 'http') +
                '://' +
                ($window.localStorage[$rootScope.localStorageKeys.lightSwitchServer.address] || '192.168.1.116') +
                ':' +
                ($window.localStorage[$rootScope.localStorageKeys.lightSwitchServer.port] || '80');
                
            return url;
        }
    }
})

// Factory used to store/retrieve data from localStorage
.factory('$localStorage', ['$window', function ($window) {
    return {
        set: function (key, value) {
            $window.localStorage[key] = value;
        },
        get: function (key, defaultValue) {
            return $window.localStorage[key] || defaultValue;
        },
        remove: function (key) {
            $window.localStorage.removeItem(key);
        },
        setObject: function (key, value) {
            $window.localStorage[key] = JSON.stringify(value);
        },
        getObject: function (key) {
            return JSON.parse($window.localStorage[key] || '{}');
        }
    }
}]);