package com.typicalfoobar.lightswitch.cordovaplugin;

import java.util.Date;

public class ClientSettings
{   
    public Server server;
    public WelcomeHomeLights welcomeHomeLights;
    
    public class Server
    {
        public String protocol;
        public String address;
        public int port;
        
        public String getUrl()
        {
            return this.protocol + "://" + this.address + ":" + this.port;
        }
    }
    
    public class WelcomeHomeLights
    {
        public Boolean active;
        public String wifiName;
        public int[] lightSwitchIdList;
        public TimeOfDay timeOfDay;
        public int minWifiDisconnectMinutes;
        
        public class TimeOfDay
        {
            public Date start;
            public Date end;
        }
        
        public String getLightSwitchIdList()
        {
            String listString = "";
            
            for (int i = 0; i < this.lightSwitchIdList.length; ++i)
            {
                listString += this.lightSwitchIdList[i];
                
                if (i != this.lightSwitchIdList.length - 1)
                {
                    listString += ",";
                }
            }
            
            return listString;
        }
    }
}