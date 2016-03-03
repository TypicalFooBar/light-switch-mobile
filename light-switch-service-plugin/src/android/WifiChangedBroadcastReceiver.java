package com.typicalfoobar.lightswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    
        if (this.isConnectedToWifiWithName(this.wifiName, context))
        {
            // Can't do networking on the main thread
            Thread thread = new Thread()
            {
                public void run()
                {
                    URL url;
                    HttpURLConnection connection = null;
                    try
                    {
                        url = new URL("http://192.168.1.116:80/api/light-switch?action=getLightSwitchList");
                        connection = (HttpURLConnection) url.openConnection();
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        
                        Log.d("WifiChangedBroadcastReceiver", "Message Received: " + response.toString());
                    }
                    catch (Exception e)
                    {
                        Log.d("WifiChangedBroadcastReceiver", "Exception: " + e.toString());
                    }
                    finally
                    {
                        if (connection != null)
                        {
                            connection.disconnect();
                        }
                    }
                }
            };
            thread.start();
        }
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