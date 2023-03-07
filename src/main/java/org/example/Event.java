package org.example;

public class Event {
    public Event(String message){
        this.message = message;
    }

    private String message;

    public String getMessage(){
        return message;
    }
}
