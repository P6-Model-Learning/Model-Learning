package aal.syslearner.Symbolic;

import aal.syslearner.Event;
import aal.syslearner.IEvent;

public class SymbolicTimedEvent implements IEvent {
    public SymbolicTimedEvent(String message, double min, double max){
        this.message = message;
        this.min = min;
        this.max = max;
    }

    private final String message;
    private double min;
    private double max;

    public String getMessage(){
        return message;
    }
    public double getMin() { return min; }
    public void setMin(double min) { this.min = min; }
    public double getMax() { return max; }
    public void setMax(double max) { this.max = max; }

    public String getSymbolicTime(){
        return min + "," + max;
    }

    @Override
    public boolean equals(Object obj){
    if (!(obj instanceof SymbolicTimedEvent)){
        return false;
    }
    SymbolicTimedEvent other = (SymbolicTimedEvent) obj;

    return this.message.equals(other.getMessage()) && this.getSymbolicTime().equals(other.getSymbolicTime());
    }

    @Override
    public String toString() {
        return message + "," + this.getSymbolicTime();
    }

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }
}
