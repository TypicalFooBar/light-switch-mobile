package com.typicalfoobar.lightswitch.cordovaplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Waits for and responds to signals from the Android OS that the network connectivity has changed.
 */
public class ConnectivityChangeBroadcastReceiver extends BroadcastReceiver
{
    /**
     * Called when connecting to or disconnecting from a router.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Log info
        Log.d("LightSwitchCordovaPlugin", "ConnectivityChangeBroadcastReceiver: onReceive() [START]");
        
        // Start a new thread to handle the state changed
        HandleConnectivityChangeRunnable runnable = new HandleConnectivityChangeRunnable(context);
        Thread thread = new Thread(runnable);
        thread.start();
        
        // Log info
        Log.d("LightSwitchCordovaPlugin", "ConnectivityChangeBroadcastReceiver: onReceive() [END]"); 
    }
}