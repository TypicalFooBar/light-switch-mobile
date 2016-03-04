package com.typicalfoobar.lightswitch;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.content.Intent;

/**
 * This plugin can start and stop the LightSwitchService.
 */
public class LightSwitchServicePlugin extends CordovaPlugin
{
    /**
     * The service Intent - used to start/stop the service.
     */
    private Intent serviceIntent = null;
    
    /**
     * Called with an action, arguments array, and a callback context
     * from JavaScript code. This is the single entry point.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "execute() start");

        // If the action is to start the service
        if(action.equals("start-service"))
        {
            // Get arguments
            String wifiName = args.getString(0);
            String lightSwitchServerUrl = args.getString(1);
            String lightSwitchIdList = args.getJSONArray(2).toString().replace("[", "").replace("]", "");
            
            // Start the service with these arguments
            this.startService(wifiName, lightSwitchServerUrl, lightSwitchIdList);
            
            // Create a JSON response to return to the view
            JSONObject response = new JSONObject();
            response.put("message", "Light Switch Service Started");
            callbackContext.success(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "execute() end");
            
            return true;
        }
        else if (action.equals("stop-service")) // If the action is to stop the service
        {
            // Stop the service
            this.stopService();
            
            // Create a JSON response to return to the view
            JSONObject response = new JSONObject();
            response.put("message", "Light Switch Service Stopped");
            callbackContext.success(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "execute() end");
            
            return true;
        }
        else // Else, the action was unrecognized
        {
            // Create a JSON response to return to the view
            JSONObject response = new JSONObject();
            response.put("message", "Unrecognized action requested.");
            callbackContext.error(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "execute() end");
            
            return false;
        }
    }
    
    /**
     * Starts the service.
     *
     * @param wifiName The wifi router's name to check when connecting to a new router.
     * @param lightSwitchServerUrl The Light Switch Server URL to connect to.
     * @param lightSwitchIdList The Light Switch IDs to turn on.
     */
    private void startService(String wifiName, String lightSwitchServerUrl, String lightSwitchIdList)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "startService() start");
        
        // Start the service
        this.serviceIntent = new Intent(this.cordova.getActivity().getBaseContext(), LightSwitchService.class);
        serviceIntent.putExtra("wifiName", wifiName);
        serviceIntent.putExtra("lightSwitchServerUrl", lightSwitchServerUrl);
        serviceIntent.putExtra("lightSwitchIdList", lightSwitchIdList);
        this.cordova.getActivity().startService(serviceIntent);
        
        // Log info
        Log.d("LightSwitchServicePlugin", "startService() end");
    }
    
    /**
     * Stops the service.
     */
    private void stopService()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "stopService() start");
        
        // Stop the service
        this.cordova.getActivity().stopService(this.serviceIntent);
        this.serviceIntent = null;
        
        // Log info
        Log.d("LightSwitchServicePlugin", "stopService() end");
    }
}