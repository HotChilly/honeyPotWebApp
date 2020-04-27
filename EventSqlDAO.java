package edu.lwtech.csd299.honeypot;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

//TODO: Create additional DAL classes for additional POJO classes
public class EventSqlDAO implements EventDAO{
    
    private static final Logger logger = Logger.getLogger(EventSqlDAO.class.getName());

    
    private static final boolean USE_AWS_DB = true;
    private Connection conn = null;
    

    public EventSqlDAO(){
        this.conn = null;
    }

    public boolean init(){
        logger.info("Connecting to the database...");

        String jdbcDriver = "org.mariadb.jdbc.Driver";
        String connString = "jdbc:mariadb://";
        if(USE_AWS_DB){
            connString += "csd299-winter-2020.cv18zcsjzteu.us-west-2.rds.amazonaws.com";
        }
        else{
            connString += "localhost";
        }
        connString += ":3306";
        connString += "/eventsdb";
        if(USE_AWS_DB){
            connString += "?user=honeypot&password=lwtech";
        }
        else{
            connString += "?user=root&password=lwtech";
        }
        

        conn = SQLUtils.connect(connString, jdbcDriver);
        if(conn == null){
            logger.error("Failed to connect to mysql database: " + connString);
            return false;
        }
        logger.info("...connected to mysql database!");

        return true;
    }

    public void reset() {
        String query = "TRUNCATE TABLE events";
        SQLUtils.executeSQL(conn, query);

        logger.info("reset complete (table has been dropped and re created)");
    }

    public boolean insertItem(Event event) {
        logger.debug("Inserting " + event + "...");

        String query = "INSERT INTO events";
        query += " (ip_address, country_code, user_agent, time, uri, query)";
        query += " VALUES (?,?,?,?,?,?)";

        List<String> allData = event.getAllData();
        int listID = SQLUtils.executeSQLInsert(conn, query, "listID",
            allData.get(0), allData.get(1), allData.get(2), allData.get(3), allData.get(4),
            allData.get(5) );
        
        logger.debug("honeypot successfully inserted with listID = " + listID);
        return true;
    }
    
    public Event getItem(int index) {
        logger.debug("Trying to get item with index: " + index);
        
        String query = "SELECT listID,";
        query += " ip_address, country_code, user_agent, time, uri, query";
        query += " FROM events WHERE listID = " + index;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);

        
        if (rows != null) {
            logger.debug("Found event!");
        } else {
            logger.debug("Did not find event.");
        }
        
        SQLRow row = rows.get(0);
        Event event = convertRowToEvent(row);
        return event;
    }
    
    public List<Event> getAllItems() {
        logger.debug("Getting all items");

        String query = "SELECT listID,";
        query += " ip_address, country_code, user_agent, time, uri, query";
        query += " FROM events ORDER BY listID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);

        if(rows==null){
            logger.info("No events found");
            return null;
        }
        List<Event> events = new ArrayList<>();
        for(SQLRow row : rows){
            Event event = convertRowToEvent(row);
            events.add(event);
        }
        return events;
    }    
    
    public int getNumItems() {
        String query = "SELECT listID FROM events ORDER BY listID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);

        if(rows==null){
            logger.info("No events found");
            return 0;
        }
        return rows.size();
    }
    
    public int getFirstItemID() {
        List<Event> events = getAllItems();
        if(events != null){
            return events.get(0).getId();
        }
        return 0;
    }

    public void disconnect() {
        SQLUtils.disconnect(conn);
        conn = null;
    }

    private Event convertRowToEvent(SQLRow row){
        int id = Integer.parseInt(row.getItem("listID"));

        String ipAddress = row.getItem("ip_address"); 
        String countryCode = row.getItem("country_code"); 
        String userAgent = row.getItem("user_agent"); 
        String time = row.getItem("time"); 
        String uri = row.getItem("uri"); 
        String queryString = row.getItem("query"); 

        Event event = new Event(id, ipAddress, countryCode, userAgent, time, uri, queryString);
        return event;
    }
}
