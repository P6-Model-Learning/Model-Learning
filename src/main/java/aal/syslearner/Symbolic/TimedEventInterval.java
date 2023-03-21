package aal.syslearner.Symbolic;

public record TimedEventInterval(int minTimestamp, int maxTimestamp, String event){
    @Override public String toString() {
        return "<" + this.event() + ",[" + this.minTimestamp + "," + this.maxTimestamp + "]" + ">";
    }
}
