package com.typicalfoobar.lightswitch;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class LightSwitchService extends Service
{
    private WifiChangedBroadcastReceiver wifiChangedBroadcastReceiver = new WifiChangedBroadcastReceiver();
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Log info
        Log.d("LightSwitchService", "onStartCommand()");
        
        String wifiName = intent.getExtras().getString("wifiName");
        
        this.wifiChangedBroadcastReceiver.setWifiName(wifiName);
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(this.wifiChangedBroadcastReceiver, intentFilter);
        
        // This will make sure the service is restarted if it is shut down by the OS
        return Service.START_REDELIVER_INTENT;
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
        // Log info
        Log.d("LightSwitchService", "onBind()");
        
        return null;
    }
    
    @Override
    public void onDestroy()
    {
        // Log info
        Log.d("LightSwitchService", "onDestroy()");
        
        this.unregisterReceiver(this.wifiChangedBroadcastReceiver);
        
        super.onDestroy();
    }
}