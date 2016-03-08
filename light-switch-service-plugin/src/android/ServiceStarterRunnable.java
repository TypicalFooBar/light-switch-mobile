package com.typicalfoobar.lightswitch;

import android.content.Context;
import android.util.Log;
import android.content.Intent;

/**
 * This object starts the service in a new thread.
 */
public class ServiceStarterRunnable implements Runnable
{
    /**
     * The application context.
     */
    private Context context;
    
    /**
     * Constructor
     *
     * @param context The application context.
     */
    public ServiceStarterRunnable(Context context)
    {
        this.context = context;
    }
    
    /**
     * Starts the service in a new thread
     */
    @Override
    public void run()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "ServiceStarterRunnable: run() [START]");
        
        // Start the service
        Intent serviceIntent = new Intent(this.context, LightSwitchService.class);
        this.context.startService(serviceIntent);
        
        // Log info
        Log.d("LightSwitchServicePlugin", "ServiceStarterRunnable: run() [END]");
    }
}