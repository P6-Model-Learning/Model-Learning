package aal.syslearner;

public interface IEvent {
    public String getMessage();
    public boolean equals(Object obj);
    public int hashCode();
}
