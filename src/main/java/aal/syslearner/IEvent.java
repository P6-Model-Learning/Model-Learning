package aal.syslearner;

public interface IEvent {
    public boolean isTerminating();
    public String getMessage();
    public boolean equals(Object obj);
    public int hashCode();
}
