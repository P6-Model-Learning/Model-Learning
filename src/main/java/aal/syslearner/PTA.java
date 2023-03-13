package aal.syslearner;

import java.util.Iterator;
import java.util.List;

public class PTA {
    private  PTANode root;

    public PTA(Event info){
        this.root = new PTANode(info);
    }

    public PTA BuildPTA(List<Trace> traces) throws Exception {
        PTA tree = this;
        PTANode currNode = tree.root;
        for(Trace trace : traces){
            currNode = tree.root;
            for(Event event : trace.getEvents()){
                PTANode node = new PTANode(event);
                if(!currNode.inChildren(node)) {
                    currNode.addChild(node);
                }
                currNode = currNode.getChild(node);
            }
        }
        return tree;
    }
}
