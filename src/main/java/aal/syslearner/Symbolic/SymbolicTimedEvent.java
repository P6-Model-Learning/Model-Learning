package aal.syslearner.Symbolic;

import aal.syslearner.Event;
import aal.syslearner.IEvent;

public class SymbolicTimedEvent implements IEvent {
    public SymbolicTimedEvent(String message, String symbolicTime){
        this.message = message;
        this.symbolicTime = symbolicTime;
    }

    private final String message;
    private final String symbolicTime;

    public String getMessage(){
        return this.message;
    }

    public String getSymbolicTime(){
        return this.symbolicTime;
    }

    @Override
    public boolean equals(Object obj){
    if (!(obj instanceof SymbolicTimedEvent)){
        return false;
    }
    SymbolicTimedEvent other = (SymbolicTimedEvent) obj;

    return this.message.equals(other.getMessage()) && this.symbolicTime == other.getSymbolicTime();
    }

    @Override
    public String toString() {
        return "SymbolicTimedEvent{" +
                "mage=" + message +
                ", symbolicTime=" + symbolicTime +
                '}';
    }

    @Override
    public int hashCode() {
        return this.message.hashCode() + this.symbolicTime.hashCode();
    }
}
