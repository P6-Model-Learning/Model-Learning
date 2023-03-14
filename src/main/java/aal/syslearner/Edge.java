package aal.syslearner;
import java.util.UUID;

public class Edge {
    private PTANode from;
    private PTANode to;
    private Event transition;
    private String id = UUID.randomUUID().toString();

    public Edge(PTANode from, PTANode to, Event transition){
        this.from = from;
        this.to = to;
        this.transition = transition;
    }

    public Event getTransition() {
        return transition;
    }
    public String getId(){
        return id;
    }

}
