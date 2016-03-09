package com.typicalfoobar.lightswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;

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
     * The hour to start using the Welcome Home Lights.
     */
    private String startHour;
    
    /**
     * Sets the hour to start using the Welcome Home Lights.
     *
     * @param startHour The hour to start using the Welcome Home Lights.
     */
    public void setStartHour(String startHour)
    {
        this.startHour = startHour;
    }
    
    /**
     * The minute (within the startHour) to start using the Welcome Home Lights.
     */
    private String startMinute;
    
    /**
     * Sets the minute (within the startHour) to start using the Welcome Home Lights.
     *
     * @param startMinute The minute (within the startHour) to start using the Welcome Home Lights.
     */
    public void setStartMinute(String startMinute)
    {
        this.startMinute = startMinute;
    }
    
    /**
     * The hour to stop using the Welcome Home Lights.
     */
    private String endHour;
    
    /**
     * Sets the hour to stop using the Welcome Home Lights.
     *
     * @param endHour The hour to stop using the Welcome Home Lights.
     */
    public void setEndHour(String endHour)
    {
        this.endHour = endHour;
    }
    
    /**
     * The minute (within the endHour) to stop using the Welcome Home Lights.
     */
    private String endMinute;
    
    /**
     * Sets the minute (within the endHour) to stop using the Welcome Home Lights.
     *
     * @param endMinute The minute (within the endHour) to stop using the Welcome Home Lights.
     */
    public void setEndMinute(String endMinute)
    {
        this.endMinute = endMinute;
    }
    
    /**
     * The number of minutes that are required to be disconnected from wifi before using the Welcome Home Lights.
     */
    private String minWifiDisconnectMinutes;
    
    /**
     * Sets the number of minutes that are required to be disconnected from wifi before using the Welcome Home Lights.
     *
     * @param minWifiDisconnectMinutes The number of minutes that are required to be disconnected from wifi before using the Welcome Home Lights.
     */
    public void setMinWifiDisconnectMinutes(String minWifiDisconnectMinutes)
    {
        this.minWifiDisconnectMinutes = minWifiDisconnectMinutes;
    }
    
    /**
     * True if the device is currently connected to the specified wifi, false otherwise.
     */
    private boolean connectedToSpecifiedWifi = false;
    
    /**
     * The time that the device disconnected from the specified wifi.
     */ 
    private Calendar wifiDisconnectedTime = Calendar.getInstance();
    
    /**
     * Called when connecting to or disconnecting from a router.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: onReceive() [START]");
        
        // If we've connected to a wifi router with the name ther user has specified
        if (this.isConnectedToWifiWithName(this.wifiName, context))
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: Connected to correct wifi");
            
            // Save the fact that we are connected to the right wifi
            this.connectedToSpecifiedWifi = true;
            
            // Get the current time
            Calendar now = Calendar.getInstance();
            
            // Set the other calendar's to compare against
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(this.startHour));
            start.set(Calendar.MINUTE, Integer.parseInt(this.startMinute));
            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(this.endHour));
            end.set(Calendar.MINUTE, Integer.parseInt(this.endMinute));
            
            // If now is greater than the start || less than end
            // Use an OR here, because once midnight hits, the check would not work if it were AND.
            // This means that if it's greater than the start on today || less than the end on today, but not inbetween these two times.
            if (now.compareTo(start) > 0 || now.compareTo(end) < 0)
            {
                // wifiDisconnectedTime + minWifiDisconnectMinutes
                Calendar disconnectTimeCheck = (Calendar)this.wifiDisconnectedTime.clone();
                disconnectTimeCheck.add(Calendar.MINUTE, Integer.parseInt(this.minWifiDisconnectMinutes));
                
                // If now is greater than (wifiDisconnectedTime + minWifiDisconnectMinutes)
                if (now.compareTo(disconnectTimeCheck) > 0)
                {
                    // Create the URL to the Light Switch Server
                    String url = this.lightSwitchServerUrl + "/api/light-switch?action=turnOn&idList=" + this.lightSwitchIdList;
                    
                    // Create the HttpRequest
                    HttpRequest httpRequest = new HttpRequest(url);
                    
                    // Get the response from the HttpRequest
                    String response = httpRequest.getResponse();
                }
            }
        }
        else
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: Disconnected from the correct wifi");
            
            // If we were connected to the correct wifi, but are no longer connected
            if (this.connectedToSpecifiedWifi)
            {
                // Log info
                Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: Device was previously connected to the correct wifi");
            
                // Save the fact that we are disconnected from the right wifi
                this.connectedToSpecifiedWifi = false;
                
                // Set the disconnected time as now
                wifiDisconnectedTime = Calendar.getInstance();
            }
            
            // Set the disconnected time as now
            wifiDisconnectedTime = Calendar.getInstance();
        }
        
        // Log info
        Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: onReceive() [END]");
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
        // Log info
        Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: isConnectedToWifiWithName() [START]");
        
        // For reasons I do not understand, This BroadcastReceiver is called EVEN WHEN it has been unregistered.
        // When that happens, it is called with a null name - this just makes sure we don't run any code if the name is null.
        // Ultimately, I'd like to figure out why this bug is happening, but for now this is the bandaid.
        if (name != null)
        {
            // Get the wifi network info
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
            
            // If we are connected to wifi
            if (networkInfo.isConnected())
            {
                // Log info
                Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: isConnectedToWifiWithName() : Connected to wifi");
                
                // Get the name of this router
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String wifiName = wifiInfo.getSSID().replaceAll("\"", "");
                
                // If the name is the same as the one the user specified
                if (wifiName.equals(name))
                {
                    // Log info
                    Log.d("WifiChangedBroadcastReceiver", "WifiChangedBroadcastReceiver: isConnectedToWifiWithName() : Wifi names match");
                    
                    return true;
                }
                else
                {
                    // Log info
                    Log.d("WifiChangedBroadcastReceiver", "WifiChangedBroadcastReceiver: isConnectedToWifiWithName() : Wifi names do not match");
                    
                    return false;
                }
            }
            else
            {
                // Log info
                Log.d("WifiChangedBroadcastReceiver", "WifiChangedBroadcastReceiver: isConnectedToWifiWithName() : Disconnected from wifi");
                
                return false;
            }
        }
        else // If the name is null
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "WifiChangedBroadcastReceiver: isConnectedToWifiWithName() [END]");
            return false;
        }
    }
}