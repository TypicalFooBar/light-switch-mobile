package com.typicalfoobar.lightswitch;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.content.Intent;

public class LightSwitchServicePlugin extends CordovaPlugin
{
    private CallbackContext callbackContext = null;
    private Intent serviceIntent = null;
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "execute() start");
        Log.d("LightSwitchServicePlugin", args.toString());
        
        // Set the callback context
        this.callbackContext = callbackContext;
        
        // Check the action
        if(action.equals("start-service"))
        {
            String wifiName = args.getString(0);
            this.startService(wifiName);
            
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
    
    private void startService(String wifiName)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "startService() start");
        Log.d("LightSwitchServicePlugin", "WifiName: " + wifiName);
        
        // Start the service
        this.serviceIntent = new Intent(this.cordova.getActivity().getBaseContext(), LightSwitchService.class);
        serviceIntent.putExtra("wifiName", wifiName);
        this.cordova.getActivity().startService(serviceIntent);
        
        // Log info
        Log.d("LightSwitchServicePlugin", "startService() end");
    }
    
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