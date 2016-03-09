package com.typicalfoobar.lightswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

public class StartServiceOnBootBroadcastReceiver extends BroadcastReceiver
{
    /**
     * Called when the OS boots up.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "StartServiceOnBootBroadcastReceiver: onReceive() [START]");
            
            // Create the database object, pointing to the localstorage sqlite database
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.ionicframework.lightswitchmobile461170/app_webview/Local Storage/file__0.localstorage", null, 0);
            
            // Create the sql statement
            String sql = "select cast(value as TEXT) from ItemTable where key='lightSwitchService-useService'";
            
            // Get the cursor
            Cursor cursor = db.rawQuery(sql, new String[0]);
            
            // Go to the first (and only) record
            cursor.moveToNext();
            
            // Get the value
            String useService = cursor.getString(0);
            
            // If useService is true
            if (useService != null && useService.equals("true"))
            {
                // Start the service
                Intent serviceIntent = new Intent(context, LightSwitchService.class);
                context.startService(serviceIntent);
            }
            
            // Log info
            Log.d("LightSwitchServicePlugin", "StartServiceOnBootBroadcastReceiver: onReceive() [END]");
        }
        catch (Exception e)
        {
            // Log info
            Log.d("LightSwitchServicePlugin", "StartServiceOnBootBroadcastReceiver: Exception: " + e.toString());
        }
    }
}