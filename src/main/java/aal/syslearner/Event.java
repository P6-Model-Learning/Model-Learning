package aal.syslearner;

import java.util.Optional;

public class Event implements IEvent{
    public Event(String message){
        this.message = message;
        this.timeStamp = 0;
    }
    public Event(String message, double timeStamp){
        this.message = message;
        this.timeStamp = timeStamp;
    }

    private final String message;
    private final double timeStamp;

    public String getMessage(){
        return message;
    }

    public double getTimeStamp() { return timeStamp; }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event)){
            return false;
        }
        Event other = (Event) obj;

        return this.message.equals(other.message) && this.timeStamp == other.timeStamp;
    }

    @Override
    public String toString() {
        return "<" + message + "," + timeStamp + ">";
    }

    @Override
    public int hashCode() {
        return this.message.hashCode() + 31 * ((int) this.timeStamp); // Not sure about this tbh
    }
}
