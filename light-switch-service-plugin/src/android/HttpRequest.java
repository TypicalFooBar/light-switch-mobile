package com.typicalfoobar.lightswitch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * This object can make simple HTTP requests.
 * The request is run in a separate thread and the response is returned using getResponse().
 */
public class HttpRequest implements Runnable
{
    /**
     * The URL to use when making the HTTP request.
     */
    private String url;
    
    /**
     * The response of the HTTP request.
     */
    private volatile String response;
    
    /**
     * Constructor
     *
     * @param url The URL to use when making the HTTP request.
     */
    public HttpRequest(String url)
    {
        this.url = url;
    }
    
    /**
     * Sends the HTTP request and sets the response.
     */
    @Override
    public void run()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "HttpRequest: run() [START]");
        Log.d("LightSwitchServicePlugin", "HttpRequest: run() : url==\"" + this.url + "\"");
        
        // Variables
        URL url;
        HttpURLConnection connection = null;
        
        try
        {
            // Set the url
            url = new URL(this.url);
            
            // Set the connection
            connection = (HttpURLConnection) url.openConnection();
            
            // Create the input stream from the connection
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            // Read the response one line at a time, appendending it to a StringBuffer, until every line has been read.
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            
            // Save the response string
            this.response = response.toString();
        }
        catch (Exception e)
        {
            // Log the exception
            Log.d("LightSwitchServicePlugin", "HttpRequest: run() : Exception - " + e.toString());
        }
        finally
        {
            // If the connection is not null
            if (connection != null)
            {
                // Disconnect from the server
                connection.disconnect();
            }
            
            // Log info
            Log.d("LightSwitchServicePlugin", "HttpRequest: run() [END]");
        }
    }
    
    /**
     * Sends the HTTP request and returns the response.
     *
     * @return The response from the HTTP request.
     */
    public String getResponse()
    {
        // Log info
        Log.d("LightSwitchServicePlugin", "HttpRequest: getResponse() [START]");
        
        try
        {
            // Create a new thread to run the HttpRequest (this object)
            Thread thread = new Thread(this);
            
            // Start the thread
            thread.start();
            
            // Wait for the thread to finish
            thread.join();
        }
        catch (Exception e)
        {
            // Log the exception
            Log.d("LightSwitchServicePlugin", "HttpRequest: run() : Exception - " + e.toString());
        }
        
        // Log info
        Log.d("LightSwitchServicePlugin", "HttpRequest: getResponse() : response==\"" + this.response + "\"");
        Log.d("LightSwitchServicePlugin", "HttpRequest: getResponse() [END]");
        
        // Return the response
        return this.response;
    }
}