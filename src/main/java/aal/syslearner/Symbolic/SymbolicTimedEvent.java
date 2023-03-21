package aal.syslearner.Symbolic;

public class SymbolicTimedEvent {
    public SymbolicTimedEvent(String event, String symbolicTime){
        this.event = event;
        this.symbolicTime = symbolicTime;
    }

    private final String event;
    private final String symbolicTime;

    public String getEvent(){
        return this.event;
    }

    public String getSymbolicTime(){
        return this.symbolicTime;
    }

    @Override
    public String toString() {
        return "SymbolicTimedEvent{" +
                "event=" + event +
                ", symbolicTime=" + symbolicTime +
                '}';
    }
}
