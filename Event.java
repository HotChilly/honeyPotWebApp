package edu.lwtech.csd299.honeypot;

import java.util.*;

//TODO: Create additional POJO classes
public class Event {
    
    // Encapsulated member variables
    // TODO: Replace these with your actual member variables
    private int id;
    private String ipAddress, countryCode, userAgent, time, uri, queryString;
    private List<String> allData = null;
  
   
    
    
    public Event(int id, String ipAddress, String countryCode, String userAgent, String time, String uri, String queryString) {

        this.id = id;
        this.ipAddress = ipAddress;
        this.countryCode = countryCode;
        this.userAgent = userAgent;
        this.time = time;
        this.uri = uri;
        this.queryString = queryString;

        allData = new ArrayList<>();

        allData.add(this.ipAddress);
        allData.add(this.countryCode);
        allData.add(this.userAgent);
        allData.add(this.time);     
        allData.add(this.uri);
        allData.add(this.queryString);
    }

    

    public int getId()
    {
        return this.id;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public String getTime() {

        return time;
    }
    public String getUri() {
        return uri;
    }
    public String getQueryString() {
        return queryString;
    }

    public List<String> getAllData(){
        return allData;
    }

    public String toString(){
        return "Event id " + getId();
    }
  
}
