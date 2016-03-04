package com.typicalfoobar.lightswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Waits for and responds to signals from the Android OS that the network connectivity has changed.
 */
public class WifiChangedBroadcastReceiver extends BroadcastReceiver
{
    /**
     * The wifi router's name to check when connecting to a new router.
     */
    private String wifiName;
    
    /**
     * Sets the wifi name to check when connecting to a new router.
     *
     * @param wifiName The wifi name to check when connecting to a new router.
     */
    public void setWifiName(String wifiName)
    {
        this.wifiName = wifiName;
    }
    
    /**
     * The Light Switch Server URL to connect to.
     */
    private String lightSwitchServerUrl;
    
    /**
     * Sets the Light Switch Server URL to connect to.
     *
     * @param lightSwitchServerUrl The Light Switch Server URL to connect to.
     */
    public void setLightSwitchServerUrl(String lightSwitchServerUrl)
    {
        this.lightSwitchServerUrl = lightSwitchServerUrl;
    }
    
    /**
     * The Light Switch IDs to turn on.
     * The strings expected format is "[n,n,...,n] (without quotes).
     */
    private String lightSwitchIdList;
    
    /**
     * Sets the Light Switch IDs to turn on.
     *
     * @param lightSwitchServerUrl The Light Switch IDs to turn on. The strings expected format is "[n,n,...,n] (without quotes).
     */
    public void setLightSwitchIdList(String lightSwitchIdList)
    {
        this.lightSwitchIdList = lightSwitchIdList;
    }
    
    /**
     * Called when connecting to or disconnecting from a router.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Log info
        Log.d("WifiChangedBroadcastReceiver", "onReceive()");
        
        // If we've connected to a wifi router with the name ther user has specified
        if (this.isConnectedToWifiWithName(this.wifiName, context))
        {
            // Create the URL to the Light Switch Server
            String url = this.lightSwitchServerUrl + "/api/light-switch?action=turnOn&idList=" + this.lightSwitchIdList;
            
            // Create the HttpRequest
            HttpRequest httpRequest = new HttpRequest(url);
            
            // Get the response from the HttpRequest
            String response = httpRequest.getResponse();
            
            // Log info
            Log.d("WifiChangedBroadcastReceiver", "Message Received: " + response);
        }
    }
    
    /**
     * Checks if the wifi router that we've connected to has the same name as the one the user specified.
     * This is also called when we disconnect from a router - this function will return false in that case.
     *
     * @param name The name to compare against when checking the routers name.
     * @param context The application context.
     */
    private boolean isConnectedToWifiWithName(String name, Context context)
    {
        // For reasons I do not understand, This BroadcastReceiver is called EVEN WHEN it has been unregistered.
        // When that happens, it is called with a null name - this just makes sure we don't run any code if the name is null.
        // Ultimately, I'd like to figure out why this bug is happening, but for now this is the bandaid.
        if (name != null)
        {
            // Log info
            Log.d("WifiChangedBroadcastReceiver", "isConnectedToWifiWithName()");
            
            // Get the wifi network info
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
            
            // If we are connected to wifi
            if (networkInfo.isConnected())
            {
                // Log info
                Log.d("WifiChangedBroadcastReceiver", "Connected to wifi");
                
                // Get the name of this router
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String wifiName = wifiInfo.getSSID().replaceAll("\"", "");
                
                // If the name is the same as the one the user specified
                if (wifiName.equals(name))
                {
                    // Log info
                    Log.d("WifiChangedBroadcastReceiver", "WifiName (" + wifiName + ") matches Name (" + name + ")");
                    
                    return true;
                }
                else
                {
                    // Log info
                    Log.d("WifiChangedBroadcastReceiver", "WifiName (" + wifiName + ") DOES NOT MATCH Name (" + name + ")");
                    
                    return false;
                }
            }
            else
            {
                // Log info
                Log.d("WifiChangedBroadcastReceiver", "Disconnected from wifi");
                
                return false;
            }
        }
        else // If the name is null
        {
            return false;
        }
    }
}