package com.typicalfoobar.lightswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartServiceOnBootBroadcastReceiver extends BroadcastReceiver
{
    /**
     * Called when the OS boots up.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "StartServiceOnBootBroadcastReceiver: onReceive() [START]");
        
        // Log info
        Log.d("LightSwitchServicePlugin", "StartServiceOnBootBroadcastReceiver: onReceive() [END]");
    }
}