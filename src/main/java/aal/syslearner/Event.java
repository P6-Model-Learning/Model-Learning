package aal.syslearner;

public class Event{
    public Event(String message){
        this.message = message;
    }

    private final String message;

    public String getMessage(){
        return message;
    }

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
