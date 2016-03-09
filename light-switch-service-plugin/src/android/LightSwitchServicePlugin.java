package com.typicalfoobar.lightswitch;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.content.Intent;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import java.lang.Thread;

/**
 * This plugin can start and stop the LightSwitchService.
 */
public class LightSwitchServicePlugin extends CordovaPlugin
{
    /**
     * Called with an action, arguments array, and a callback context
     * from JavaScript code. This is the single entry point.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() [START]");

        // If the action is to start the service
        if(action.equals("start-service"))
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() : action==\"start-service\"");
            
            // Start the service
            Intent serviceIntent = new Intent(this.cordova.getActivity().getBaseContext(), LightSwitchService.class);
            this.cordova.getActivity().startService(serviceIntent);
            
            // Create a JSON response to return to the view
            JSONObject response = new JSONObject();
            response.put("message", "Light Switch Service Started");
            callbackContext.success(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() [END]");
            
            return true;
        }
        else if (action.equals("stop-service")) // If the action is to stop the service
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() : action==\"stop-service\"");
            
            // Stop the service
            this.cordova.getActivity().stopService(new Intent(this.cordova.getActivity().getBaseContext(), LightSwitchService.class));
            
            // Create a JSON response to return to the view
            JSONObject response = new JSONObject();
            response.put("message", "Light Switch Service Stopped");
            callbackContext.success(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() [END]");
            
            return true;
        }
        else if (action.equals("is-service-running")) // If the action is to check if the service is running
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() : action==\"is-service-running\"");
            
            // Create a JSON response to return to the view
            JSONObject response = new JSONObject();
            response.put("isServiceRunning", isServiceRunning(LightSwitchService.class));
            callbackContext.success(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() [END]");
            
            return true;
        }
        else // Else, the action was unrecognized
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() : Unrecognized action");
            
            // Create a JSON response to return to the view
            JSONObject response = new JSONObject();
            response.put("message", "Unrecognized action requested.");
            callbackContext.error(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: execute() [END]");
            
            return false;
        }
    }
    
    /**
     * Checks if service is running.
     *
     * @param serviceClass The service's class to check for.
     *
     * @return True if the service is running, false otherwise.
     */
    private boolean isServiceRunning(Class<?> serviceClass)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: isServiceRunning() [START]");
        
        ActivityManager manager = (ActivityManager) this.cordova.getActivity().getBaseContext().getSystemService(this.cordova.getActivity().getBaseContext().ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                // Log info
                Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: isServiceRunning() [END]");
                
                // Service is running
                return true;
            }
        }
        
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchServicePlugin: isServiceRunning() [END]");
        
        // Service is not running
        return false;
    }
}