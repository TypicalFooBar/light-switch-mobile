package com.typicalfoobar.lightswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiChangedBroadcastReceiver extends BroadcastReceiver
{
    private String wifiName;
    public void setWifiName(String wifiName)
    {
        this.wifiName = wifiName;
    }
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Log info
        Log.d("WifiChangedBroadcastReceiver", "onReceive()");
        
        this.isConnectedToWifiWithName(this.wifiName, context);
    }
    
    private boolean isConnectedToWifiWithName(String name, Context context)
    {
        if (name != null)
        {
            // Log info
            Log.d("WifiChangedBroadcastReceiver", "isConnectedToWifiWithName()");
            
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
            
            if (networkInfo.isConnected())
            {
                // Log info
                Log.d("WifiChangedBroadcastReceiver", "Connected to wifi");
                
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String wifiName = wifiInfo.getSSID();
                
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
        else
        {
            return false;
        }
    }
}