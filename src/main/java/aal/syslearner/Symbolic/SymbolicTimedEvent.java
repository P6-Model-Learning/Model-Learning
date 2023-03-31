package aal.syslearner.Symbolic;

import aal.syslearner.Event;
import aal.syslearner.IEvent;

public class SymbolicTimedEvent implements IEvent {
    public SymbolicTimedEvent(String message, String symbolicTime){
        this.message = message;
        this.symbolicTime = symbolicTime;
        this.terminating = false;
    }

    public SymbolicTimedEvent(String message, String symbolicTime, boolean terminating) {
        this.message = message;
        this.symbolicTime = symbolicTime;
        this.terminating = terminating;
    }

    private final String message;
    private final String symbolicTime;
    private final boolean terminating;

    public String getMessage(){
        return message;
    }

    public String getSymbolicTime(){
        return symbolicTime;
    }

    public boolean isTerminating() { return terminating; }

    @Override
    public boolean equals(Object obj){
    if (!(obj instanceof SymbolicTimedEvent)){
        return false;
    }
    SymbolicTimedEvent other = (SymbolicTimedEvent) obj;

    return this.message.equals(other.getMessage()) && this.symbolicTime.equals(other.getSymbolicTime());
    }

    @Override
    public String toString() {
        return message + "," + symbolicTime;
    }

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }
}
