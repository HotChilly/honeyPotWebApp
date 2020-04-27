package edu.lwtech.csd299.honeypot;

import java.util.*;

import org.apache.log4j.Logger;

//TODO: Create additional DAL classes for additional POJO classes
public class EventMemoryDAO implements EventDAO{
    
    private static final Logger logger = Logger.getLogger(EventMemoryDAO.class.getName());
    
  
    private List<Event> memoryDB;
    
    public EventMemoryDAO(){
        this.memoryDB = new ArrayList<>();
    }

    public void reset(){
        this.memoryDB = new ArrayList<>();
    }

    public boolean init() {
        return true;            // Everything was initialized in the constructor
    }
  

    public boolean insertItem(Event item) {
        logger.debug("Inserting " + item + "...");

        // if (item.getId() != -1) {
        //     logger.error("Attempting to add previously added item: " + item);
        //     return false;
        // }
        
        //item = new Event(generateNextItemID(), item.getName());
        memoryDB.add(item);
        
        logger.debug("Item successfully inserted!");
        return true;
    }
    
    public Event getItem(int index) {
        logger.debug("Trying to get item with index: " + index);
        
        Event item = memoryDB.get(index);
        
        if (item != null) {
            logger.debug("Found item!");
        } else {
            logger.debug("Did not find item.");
        }
        
        return item;
    }
    
    public List<Event> getAllItems() {
        logger.debug("Getting all items");
        return new ArrayList<>(memoryDB);
    }    
    
    public int getNumItems() {
        return memoryDB.size();
    }
    
    public int getFirstItemID() {
        Event item = memoryDB.get(0);
        return item.getId();
    }

    public void disconnect() {
        this.memoryDB = null;
    }
    
}
