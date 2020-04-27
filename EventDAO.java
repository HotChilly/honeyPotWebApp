package edu.lwtech.csd299.honeypot;

import java.util.*;

public interface EventDAO{

    public boolean init();
    public boolean insertItem(Event event);
    public int getNumItems();
    public void reset();
    List<Event> getAllItems();
    public void disconnect();
}