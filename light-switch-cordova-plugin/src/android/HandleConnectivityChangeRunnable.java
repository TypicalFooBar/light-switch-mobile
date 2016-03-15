package com.typicalfoobar.lightswitch.cordovaplugin;

import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HandleConnectivityChangeRunnable implements Runnable
{
    private Context context;
    private Gson gson;
    
    public HandleConnectivityChangeRunnable(Context context)
    {
        this.context = context;
        
        // Treat dates using the same date format as what is saved by JavaScript to the database
        this.gson = new GsonBuilder().setDateFormat("EEE MMM dd yyyy HH:mm:ss z").create();
    }
    
    @Override
    public void run()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: onReceive() [START]");
        
        // Create the object to set/get info to/from the local storage SQLite database
        LocalStorage localStorage = new LocalStorage();
        
        // Make sure the client settings
        if (localStorage.clientSettings != null)
        {
            // If the Welcome Home Lights are active
            if (localStorage.clientSettings.welcomeHomeLights.active)
            {
                // Log info
                Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: Welcome Home Lights are active");
                
                // If the WifiStateInfo object is null, create a new one
                if (localStorage.wifiStateInfo == null)
                {
                    localStorage.wifiStateInfo = new WifiStateInfo();
                }
                
                // If we've connected to a wifi router with the name ther user has specified
                if (getWifiName(context).equals(localStorage.clientSettings.welcomeHomeLights.wifiName))
                {
                    // Log info
                    Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: Wifi name matches");
                    
                    // Save the fact that we are connected to the right wifi
                    localStorage.wifiStateInfo.connectedToSpecifiedWifi = true;
                    localStorage.updateInDb("WifiStateInfo", this.gson.toJson(localStorage.wifiStateInfo, WifiStateInfo.class));
                    
                    // Check if it's the right time to use the Welcome Home Lights
                    if (isBetweenStartAndEndTimes(localStorage.clientSettings.welcomeHomeLights.timeOfDay.start, localStorage.clientSettings.welcomeHomeLights.timeOfDay.end, localStorage.wifiStateInfo.lastWifiDisconnectedTime, localStorage.clientSettings.welcomeHomeLights.minWifiDisconnectMinutes))
                    {
                        // Create the URL to the Light Switch Server
                        String url = localStorage.clientSettings.server.getUrl() + "/api/light-switch?action=turnOn&idList=" + localStorage.clientSettings.welcomeHomeLights.getLightSwitchIdList();
                        
                        // Create the HttpRequest
                        HttpRequest httpRequest = new HttpRequest(url);
                        
                        // Get the response from the HttpRequest
                        String response = httpRequest.getResponse();
                    }
                    
                    // Log info
                    Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: WifiStateJson=" + this.gson.toJson(localStorage.wifiStateInfo, WifiStateInfo.class));
                }
                else
                {
                    // Used to make sure we don't insert twice
                    Boolean setLastWifiDisconnectedTime = false;
                    
                    // Log info
                    Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: Disconnected from the correct wifi");
                    
                    // If we were connected to the correct wifi, but are no longer connected
                    if (localStorage.wifiStateInfo.connectedToSpecifiedWifi == true)
                    {
                        // Log info
                        Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: Device was previously connected to the correct wifi");
                    
                        // Save info
                        localStorage.wifiStateInfo.connectedToSpecifiedWifi = false; // Save the fact that we are disconnected from the right wifi
                        localStorage.wifiStateInfo.lastWifiDisconnectedTime = Calendar.getInstance().getTime(); // Set the disconnected time as now
                        localStorage.updateInDb("WifiStateInfo", this.gson.toJson(localStorage.wifiStateInfo, WifiStateInfo.class)); // Commit to the database
                        
                        // We have saved the last time we were disconnected from the wifi in the database
                        setLastWifiDisconnectedTime = true;
                    }

                    // If we didn't save the last time we were disconnected from the database
                    if (!setLastWifiDisconnectedTime)
                    {
                        // Save the disconnected time as now
                        localStorage.wifiStateInfo.connectedToSpecifiedWifi = false; // Save the fact that we are disconnected from the right wifi
                        localStorage.wifiStateInfo.lastWifiDisconnectedTime = Calendar.getInstance().getTime(); // Set the disconnected time as now
                        localStorage.updateInDb("WifiStateInfo", this.gson.toJson(localStorage.wifiStateInfo, WifiStateInfo.class)); // Commit to the database
                    }
                }
            }
        }
        
        // Log info
        Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: onReceive() [END]");
    }
    
    private Boolean isBetweenStartAndEndTimes(Date startTime, Date endTime, Date lastWifiDisconnectedTime, int minWifiDisconnectMinutes)
    {
        // Get the current time
        Calendar now = Calendar.getInstance();
        
        // Set the other calendar's to compare against.
        // setTime() will set the correct values for the time of day, but will also set it to the date 1970-01-01.
        // To fix this, we need to set the year, month, and day of month after the time has been set correctly.
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        start.set(Calendar.YEAR, now.get(Calendar.YEAR));
        start.set(Calendar.MONTH, now.get(Calendar.MONTH));
        start.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        end.set(Calendar.YEAR, now.get(Calendar.YEAR));
        end.set(Calendar.MONTH, now.get(Calendar.MONTH));
        end.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        
        // If now is greater than the start || less than end
        // Use an OR here, because once midnight hits, the check would not work if it were AND.
        // This means that if it's greater than the start on today || less than the end on today, but not inbetween these two times.
        if (now.compareTo(start) > 0 || now.compareTo(end) < 0)
        {
            // wifiDisconnectedTime + minWifiDisconnectMinutes
            Calendar disconnectTimeCheck = Calendar.getInstance();
            disconnectTimeCheck.setTime(lastWifiDisconnectedTime);
            disconnectTimeCheck.set(Calendar.YEAR, now.get(Calendar.YEAR));
            disconnectTimeCheck.set(Calendar.MONTH, now.get(Calendar.MONTH));
            disconnectTimeCheck.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
            disconnectTimeCheck.add(Calendar.MINUTE, minWifiDisconnectMinutes);
            
            // If now is greater than (wifiDisconnectedTime + minWifiDisconnectMinutes)
            if (now.compareTo(disconnectTimeCheck) > 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    private String getWifiName(Context context)
    {
        String wifiName = "";
        
        // Get the wifi network info
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
        
        // If we are connected to wifi
        if (networkInfo.isConnected())
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "HandleConnectivityChangeRunnable: isConnectedToWifiWithName() : Connected to wifi");
            
            // Get the name of this router
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            wifiName = wifiInfo.getSSID().replaceAll("\"", "");
        }
        
        return wifiName;
    }
}