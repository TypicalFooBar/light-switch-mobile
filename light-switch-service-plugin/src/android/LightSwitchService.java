package com.typicalfoobar.lightswitch;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * This service starts a BroadcastListener to monitor changes in network connectivity.
 * A signal will be sent to the Light Switch Server if the right wifi router is connected to.
 */
public class LightSwitchService extends Service
{
    /**
     * The BroadcastReceiver which will monitor changes in network connectivity.
     */
    private WifiChangedBroadcastReceiver wifiChangedBroadcastReceiver = new WifiChangedBroadcastReceiver();
    
    /**
     * Called once when the service is started.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onStartCommand() [START]");
        
        // Get arguments
        String wifiName = intent.getExtras().getString("wifiName");
        String lightSwitchServerUrl = intent.getExtras().getString("lightSwitchServerUrl");
        String lightSwitchIdList = intent.getExtras().getString("lightSwitchIdList");
        
        // Set values in the BroadcastReceiver
        this.wifiChangedBroadcastReceiver.setWifiName(wifiName);
        this.wifiChangedBroadcastReceiver.setLightSwitchServerUrl(lightSwitchServerUrl);
        this.wifiChangedBroadcastReceiver.setLightSwitchIdList(lightSwitchIdList);
        
        // Create the intent for the BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        
        // Register the BroadcastReceiver
        this.registerReceiver(this.wifiChangedBroadcastReceiver, intentFilter);
        
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onStartCommand() [END]");
        
        // This will make sure the service is restarted if it is shut down by the OS
        // and it will be passed the same intent that started the service the first time.
        return Service.START_REDELIVER_INTENT;
    }
    
    /**
     * Called when bound to a new broadcast.
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onBind() [START]");
        
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onBind() [END]");
        
        return null;
    }
    
    /**
     * Called once when the service is stopped.
     */
    @Override
    public void onDestroy()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onDestroy() [START]");
        
        // Unregister the BroadcastReceiver
        this.unregisterReceiver(this.wifiChangedBroadcastReceiver);
        
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onDestroy() [END]");
        
        super.onDestroy();
    }
}