angular.module('light-switch-mobile.controllers', [])

// Add the global variables to the rootScope
.run(function($rootScope) {
    // Set root-scope variables
    $rootScope.lightSwitchUrl = '192.168.1.116';
})

// Factory used to store/retrieve data from localStorage
.factory('$localStorage', ['$window', function($window) {
  return {
    set: function(key, value) {
      $window.localStorage[key] = value;
    },
    get: function(key, defaultValue) {
      return $window.localStorage[key] || defaultValue;
    },
    remove: function(key) {
      $window.localStorage.removeItem(key);
    },
    setObject: function(key, value) {
      $window.localStorage[key] = JSON.stringify(value);
    },
    getObject: function(key) {
      return JSON.parse($window.localStorage[key] || '{}');
    }
  }
}]);