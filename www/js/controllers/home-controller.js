angular.module('light-switch-mobile.controllers')

.controller('HomeController', function($scope, $localStorage, $location) {
    $scope.lightSwitches = [
        {
            id: 1,
            name: "Floor Lamp",
            active: true,
            pinNumber: 0
        }
    ];
});