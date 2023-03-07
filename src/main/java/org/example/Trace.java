package org.example;

import java.util.ArrayList;
import java.util.List;

public class Trace{
    public Trace(List<Event> events){
        id = idCount++;
        this.events = events;
    }

    private static int idCount = 1;
    private int id;
    private List<Event> events;

    public int getId(){
        return id;
    }

    public List<Event> getEvents(){
        return events;
    }
}
