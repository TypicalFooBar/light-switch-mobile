package com.typicalfoobar.lightswitch.cordovaplugin;

import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.lang.Thread;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LocalStorage
{
    public ClientSettings clientSettings = null;
    public WifiStateInfo wifiStateInfo = null;
    
    private Gson gson;
    
    public LocalStorage()
    {
        // Treat dates using the same date format as what is saved by JavaScript to the database
        this.gson = new GsonBuilder().setDateFormat("EEE MMM dd yyyy HH:mm:ss z").create();
        
        this.clientSettings = getFromDb("ClientSettings", ClientSettings.class);
        this.wifiStateInfo = getFromDb("WifiStateInfo", WifiStateInfo.class);
    }
    
    private <T> T getFromDb(String key, Class<T> type)
    {
        T objToReturn = null;
        
        try
        {
            // Create the database object, pointing to the localstorage sqlite database
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.ionicframework.lightswitchmobile461170/app_webview/Local Storage/file__0.localstorage", null, 0);
            
            // Create the sql statement
            String sql = "select cast(Value as text) from ItemTable where Key = '" + key + "'";
            
            // Get the cursor
            Cursor cursor = db.rawQuery(sql, new String[0]);
            
            // Go to the first (and only) record
            cursor.moveToNext();
            
            // Get the values
            objToReturn = this.gson.fromJson(cursor.getString(0), type);

            // Close the database
            db.close();
        }
        catch (Exception e)
        {
            // Log info
            Log.d("LightSwitchCordovaPlugin", "LocalStorage: populateFromDb(): Exception caught (" + e.toString() + ")");
        }

        // Return the object
        return objToReturn;
    }
    
    public void updateInDb(String key, String value)
    {
        try
        {
            // Create the database object, pointing to the localstorage sqlite database
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.ionicframework.lightswitchmobile461170/app_webview/Local Storage/file__0.localstorage", null, 0);
            
            // Log info
            Log.d("LightSwitchCordovaPlugin", "LocalStorage: updateConnectedToSpecifiedWifiInDb(): Database connection open:" + db.isOpen());
            
            // Create the sql statement
            String updateSql = "update ItemTable set Value = '" + value + "' where Key = '" + key + "'";
            String insertSql = "insert or ignore into ItemTable (key, value) values ('" + key + "', '" + value + "')";
                
            db.execSQL(updateSql);
            db.execSQL(insertSql);
            
            db.close();
        }
        catch (Exception e)
        {
            // Log info
            Log.d("LightSwitchCordovaPlugin", "LocalStorage: updateLastWifiDisconnectedTimeInDb(): Exception caught (" + e.toString() + ")");
        }
    }
}