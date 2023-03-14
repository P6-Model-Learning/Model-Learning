package aal.syslearner;

import net.automatalib.graphs.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class PTA implements Graph<PTANode, Edge> {
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

    @Override
    public Collection<Edge> getOutgoingEdges(PTANode node) {
        return node.getEdges();
    }

    @Override
    public PTANode getTarget(Edge edge) {
        return root.getTarget(edge, this.root);
    }

    @Override
    public Collection<PTANode> getNodes() {
        Collection<PTANode> nodes = new ArrayList<>();
        Stack<PTANode> processNodes = new Stack<>();
        PTANode node = root;
        processNodes.push(node);
        while(!(processNodes.empty())) {
            node = processNodes.pop();
            nodes.add(node);
            for (PTANode child : node.getChildren()) {
                processNodes.push(child);
            }
        }
        return nodes;
    }
}
