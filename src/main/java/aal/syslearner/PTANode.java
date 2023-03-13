package aal.syslearner;

import java.util.ArrayList;
import java.util.List;

public class PTANode {
    private PTANode parent;
    private List<PTANode> children = new ArrayList<PTANode>() {
    };
    private Event info;

    public PTANode(Event info){
        this.info = info;
    }
    public void addChild(PTANode child){
        child.parent = this;
        this.children.add(child);
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
        throw new RuntimeException("Node not in children");
    }
    public PTANode getParent() {
        return parent;
    }

    public Event getInfo() {
        return info;
    }
}
