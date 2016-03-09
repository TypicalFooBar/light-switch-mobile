package com.typicalfoobar.lightswitch;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.lang.Thread;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    
    private class StartServiceRunnable implements Runnable
    {
        private WifiChangedBroadcastReceiver wifiChangedBroadcastReceiver;
        private Service parentService;
        
        public StartServiceRunnable(WifiChangedBroadcastReceiver wifiChangedBroadcastReceiver, Service parentService)
        {
            this.wifiChangedBroadcastReceiver = wifiChangedBroadcastReceiver;
            this.parentService = parentService;
        }
        
        @Override
        public void run()
        {
            // Settings that will be sent to the WifiChangedBroadcastReceiver
            String wifiName = null;
            String lightSwitchServerUrl = null;
            String lightSwitchIdList = null;
            String startHour = null;
            String startMinute = null;
            String endHour = null;
            String endMinute = null;
            String minWifiDisconnectMinutes = null;
            
            // Keep track of how many times we've attempted to connect to the database.
            int tryCount = 0;
            
            // On the first run of the application, the database may not yet be written to disk - we'll loop until it is.
            while (tryCount < 4)
            {
                try
                {
                    // Sleep for a bit and then proceed to try
                    try { Thread.sleep(1500); } catch (Exception e) {}
                    
                    // If we hit our try limit
                    if (tryCount >= 3)
                    {
                        // Kill the service
                        this.parentService.stopSelf();
                        return;
                    }
                    
                    // Create the database object, pointing to the localstorage sqlite database
                    SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.ionicframework.lightswitchmobile461170/app_webview/Local Storage/file__0.localstorage", null, 0);
                    
                    // Log info
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): Database connection open:" + db.isOpen());
                    
                    // Create the sql statement
                    String sql =
                        "select" + 
                        "(" + 
                        "    select cast(value as TEXT) from ItemTable where key='lightSwitchService-WifiName'" + 
                        ") as WifiName," + 
                        "(" + 
                        "    select Protocol || '://' || Address || ':' || Port from" + 
                        "    (" + 
                        "        select" + 
                        "        (" + 
                        "            select cast(value as TEXT) from ItemTable where key='lightSwitchServer-Protocol'" + 
                        "        ) as Protocol," + 
                        "        (" + 
                        "            select cast(value as TEXT) from ItemTable where key='lightSwitchServer-Address'" + 
                        "        ) as Address," + 
                        "        (" + 
                        "            select cast(value as TEXT) from ItemTable where key='lightSwitchServer-Port'" + 
                        "        ) as Port" + 
                        "    )" + 
                        ") as LightSwitchServerUrl," + 
                        "(" + 
                        "    select cast(value as TEXT) from ItemTable where key='lightSwitchService-welcomeHomeLights-lightSwitchIdList'" + 
                        ") as LightSwitchIdList," + 
                        "(" + 
                        "    select cast(value as TEXT) from ItemTable where key='lightSwitchService-welcomeHomeLights-timeOfDay-start'" + 
                        ") as StartTime," + 
                        "(" + 
                        "    select cast(value as TEXT) from ItemTable where key='lightSwitchService-welcomeHomeLights-timeOfDay-end'" + 
                        ") as EndTime," + 
                        "(" + 
                        "    select cast(value as TEXT) from ItemTable where key='lightSwitchService-welcomeHomeLights-minWifiDisconnectMinutes'" + 
                        ") as MinWifiDisconnectMinutes";
                    
                    // Get the cursor
                    Cursor cursor = db.rawQuery(sql, new String[0]);
                    
                    // Go to the first (and only) record
                    cursor.moveToNext();
                    
                    // Get the name of the wifi
                    wifiName = cursor.getString(0);
                    lightSwitchServerUrl = cursor.getString(1);
                    lightSwitchIdList = cursor.getString(2);
                    String startTime = cursor.getString(3);
                    String endTime = cursor.getString(4);
                    minWifiDisconnectMinutes = cursor.getString(5);
                    
                    // Log info
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): wifiName=" + wifiName);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): lightSwitchServerUrl=" + lightSwitchServerUrl);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): lightSwitchIdList=" + lightSwitchIdList);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): startTime=" + startTime);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): endTime=" + endTime);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): minWifiDisconnectMinutes=" + minWifiDisconnectMinutes);
                    
                    // If the wifiName is null, then let's retry connecting to the database (it may not have been written to, yet)
                    if (wifiName == null)
                    {
                        throw new Exception("Table not populated, yet.");
                    }
                    
                    // Close the database
                    db.close();
                    
                    // The patter to look for in the time strings
                    Pattern timePattern = Pattern.compile("(..):");
                    
                    // Find the startHour and startMinute
                    Matcher startMatcher = timePattern.matcher(startTime);
                    startMatcher.find();
                    startHour = startMatcher.group(1);
                    startMatcher.find();
                    startMinute = startMatcher.group(1);
                    
                    // Find the endHour and endMinute
                    Matcher endMatcher = timePattern.matcher(endTime);
                    endMatcher.find();
                    endHour = endMatcher.group(1);
                    endMatcher.find();
                    endMinute = endMatcher.group(1);
                    
                    // Log info
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): startHour=" + startHour);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): startMinute=" + startMinute);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): endHour=" + endHour);
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): endMinute=" + endMinute);
                    
                    // Leave the while loop
                    break;
                }
                catch (Exception e)
                {
                    // Log info
                    Log.d("LightSwitchServicePlugin", "StartServiceRunnable: run(): Exception caught (" + e.toString() + ") - retrying");
                    
                    // Up the try count
                    tryCount++;
                }
            }
            
            // Set values in the BroadcastReceiver
            this.wifiChangedBroadcastReceiver.setWifiName(wifiName);
            this.wifiChangedBroadcastReceiver.setLightSwitchServerUrl(lightSwitchServerUrl);
            this.wifiChangedBroadcastReceiver.setLightSwitchIdList(lightSwitchIdList);
            this.wifiChangedBroadcastReceiver.setStartHour(startHour);
            this.wifiChangedBroadcastReceiver.setStartMinute(startMinute);
            this.wifiChangedBroadcastReceiver.setEndHour(endHour);
            this.wifiChangedBroadcastReceiver.setEndMinute(endMinute);
            this.wifiChangedBroadcastReceiver.setMinWifiDisconnectMinutes(minWifiDisconnectMinutes);
            
            // Create the intent for the BroadcastReceiver
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            
            // Register the BroadcastReceiver
            this.parentService.registerReceiver(this.wifiChangedBroadcastReceiver, intentFilter);
        }
    }
    
    /**
     * Called once when the service is started.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onStartCommand() [START]");
        
        // Run the setup code in a new thread (so we don't lock up the main thread)
        StartServiceRunnable startServiceRunnable = new StartServiceRunnable(this.wifiChangedBroadcastReceiver, this);
        Thread thread = new Thread(startServiceRunnable);
        thread.start();
        
        // Log info
        Log.d("LightSwitchServicePlugin", "LightSwitchService: onStartCommand() [END]");
        
        // This will make sure the service is restarted if it is shut down by the OS
        return Service.START_STICKY;
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