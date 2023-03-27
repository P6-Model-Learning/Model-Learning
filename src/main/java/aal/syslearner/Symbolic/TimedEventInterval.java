package aal.syslearner.Symbolic;

public class TimedEventInterval{
    public TimedEventInterval(double minTimestamp, double maxTimestamp, String message){
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
        this.message = message;
    }

    private double minTimestamp;
    private double maxTimestamp;
    private final String message;

    public double getMinTimestamp(){
        return minTimestamp;
    }

    public void setMinTimeStamp(double minTimestamp){
        this.minTimestamp = minTimestamp;
    }

    public double getMaxTimestamp(){
        return maxTimestamp;
    }

    public void setMaxTimestamp(double maxTimestamp){
        this.maxTimestamp = maxTimestamp;
    }

    public String getMessage(){
        return message;
    }

    public String getSymbolicTime(){
        return this.minTimestamp + "," + this.maxTimestamp;
    }

    @Override public String toString() {
        return "<" + this.message + ",[" + this.minTimestamp + "," + this.maxTimestamp + "]" + ">";
    }

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }
}
