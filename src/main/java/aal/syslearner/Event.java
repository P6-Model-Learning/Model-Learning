package aal.syslearner;

import java.util.Optional;

public class Event implements IEvent{
    public Event(String message){
        this.message = message;
        this.timestamp = 0;
    }
    public Event(String message, double timeStamp){
        this.message = message;
        this.timestamp = timeStamp;
    }

    private final String message;
    private final double timestamp;

    public String getMessage(){ return message; }

    public double getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event)){
            return false;
        }
        Event other = (Event) obj;

        return this.message.equals(other.message);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }
}
