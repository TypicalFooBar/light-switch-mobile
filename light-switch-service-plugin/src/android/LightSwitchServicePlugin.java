package com.typicalfoobar.lightswitch;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class LightSwitchServicePlugin extends CordovaPlugin
{
    private CallbackContext callbackContext = null;
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "execute() start");
        
        // Set the callback context
        this.callbackContext = callbackContext;
        
        // Check the action
        if(action.equals("start-service"))
        {
            this.startService();
            
            JSONObject response = new JSONObject();
            response.put("message", "Light Switch Service Started");
            this.callbackContext.success(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "execute() end");
            
            return true;
        }
        else if (action.equals("stop-service"))
        {
            this.stopService();
            
            JSONObject response = new JSONObject();
            response.put("message", "Light Switch Service Stopped");
            this.callbackContext.success(response);
            
            // Log info
            Log.d("LightSwitchServicePlugin", "execute() end");
            
            return true;
        }
        
        JSONObject response = new JSONObject();
        response.put("message", "Unrecognized action requested.");
        this.callbackContext.error(response);
        
        // Log info
        Log.d("LightSwitchServicePlugin", "execute() end");
        
        return false;
    }
    
    private void startService()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "startService() start");
        
        // Log info
        Log.d("LightSwitchServicePlugin", "startService() end");
    }
    
    private void stopService()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "stopService() start");
        
        // Log info
        Log.d("LightSwitchServicePlugin", "stopService() end");
    }
}