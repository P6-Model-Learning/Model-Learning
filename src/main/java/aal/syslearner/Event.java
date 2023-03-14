package aal.syslearner;

import de.learnlib.datastructure.pta.pta.AbstractBasePTAState;

public class Event extends AbstractBasePTAState<Object, Object, Event> {
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
    protected Event createState() {
        return null;
    }
}
