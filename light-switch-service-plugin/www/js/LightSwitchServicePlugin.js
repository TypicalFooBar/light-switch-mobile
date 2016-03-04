var LightSwitchServicePlugin = {
    startService: function(successCallback, errorCallback, wifiName, lightSwitchServerUrl, lightSwitchIdList) {
        cordova.exec(
            successCallback, // Success callback
            errorCallback, // Error callback
            "LightSwitchServicePlugin", // Mapped to the native Java class that will be called
            "start-service", // Send this action to the execute() function in the plugin
            [wifiName, lightSwitchServerUrl, lightSwitchIdList]); // Arguments, if needed
    },
    stopService: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback, // Success callback
            errorCallback, // Error callback
            "LightSwitchServicePlugin", // Mapped to the native Java class that will be called
            "stop-service", // Send this action to the execute() function in the plugin
            []); // Arguments, if needed
    },
    isServiceRunning: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback, // Success callback
            errorCallback, // Error callback
            "LightSwitchServicePlugin", // Mapped to the native Java class that will be called
            "is-service-running", // Send this action to the execute() function in the plugin
            []); // Arguments, if needed
    }
}

module.exports = LightSwitchServicePlugin;