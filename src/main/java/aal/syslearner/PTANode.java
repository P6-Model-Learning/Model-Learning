package aal.syslearner;

import java.util.ArrayList;
import java.util.List;

public class PTANode {
    private PTANode parent;
    private List<PTANode> children = new ArrayList<PTANode>() {
    };
    private Event info;
    private List<Edge> edges = new ArrayList<Edge>(){};

    public PTANode(Event info){
        this.info = info;
    }
    public void addChild(PTANode child){
        child.parent = this;
        this.children.add(child);
        Edge edge = new Edge(this, child, this.info);
        this.edges.add(edge);
    }

    public List<PTANode> getChildren() {
        return children;
    }
    public Boolean inChildren(PTANode node){
        for(PTANode child : this.children){
            if (child.getInfo().getMessage().equals(node.getInfo().getMessage())){
                return true;
            }
        }
        return false;
    }

    public PTANode getChild(PTANode node){
        for(PTANode child : this.children){
            if(child.getInfo().getMessage().equals(node.getInfo().getMessage())){
                return child;
            }
        }
        return null;
    }

    public List<Edge> getEdges(){
        return this.edges;
    }
    public Edge getEdge(String id){
        for(Edge edge : this.edges){
            if(edge.getId().equals(id)){
                return edge;
            }
        }
        return null;
    }
    public boolean inEdges(Edge edge){
        if (this.edges.contains(edge)){
            return true;
        }else{
            return false;
        }
    }
    public PTANode getTarget(Edge edge, PTANode root){
        if(this.inEdges(edge)){
            return this;
        }
        for(PTANode child : root.children){
            child.getTarget(edge, child);
        }
        return null;
    }
    public PTANode getParent() {
        return parent;
    }

    public Event getInfo() {
        return info;
    }
}
