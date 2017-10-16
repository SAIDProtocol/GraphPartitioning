/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icn.graphpubsub.graphpartitioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Jiachen Chen
 */
public class GraphNode {

    private final String name;
    /**
     * Individual weight: the # of messages sent to the node itself Incoming
     * weight: the # of messages from other RPs that send to the node Total
     * weight: the # of messages that are sent to this node because of itself
     * and ancestors the # of messages this RP has to process for this node
     * Outgoing count: the # of *child nodes* that are on other RPS outgoing
     * weight = outgoing count * total weight
     */
    private final int individualWeight, incomingWeight, outgoingCount;
    private int totalWeight;

    public GraphNode(String name, int individualWeight, int incomingWeight, int outgoingCount) {
        this.name = name;
        this.individualWeight = individualWeight;
        this.incomingWeight = incomingWeight;
        this.outgoingCount = outgoingCount;
    }

    public String getName() {
        return name;
    }

    public int getIndividualWeight() {
        return individualWeight;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public int getIncomingWeight() {
        return incomingWeight;
    }

    public int getOutgoingCount() {
        return outgoingCount;
    }

    public int getOutgoingWeight() {
        return outgoingCount * totalWeight;
    }

    public int addTotalWeight(int weight) {
        totalWeight += weight;
        return totalWeight;
    }

    private final HashSet<GraphNode> parents = new HashSet<>();
    private final HashSet<GraphNode> children = new HashSet<>();

    public boolean addChild(GraphNode child) {
        if (!children.add(child)) {
            return false;
        }
        child.parents.add(this);
        return true;
    }

    public boolean removeChild(GraphNode child) {
        if (!children.remove(child)) {
            return false;
        }
        child.parents.remove(this);
        return true;
    }

    public void forEachParent(Consumer<? super GraphNode> action) {
        parents.forEach(action);
    }

    public void forEachChildren(Consumer<? super GraphNode> action) {
        children.forEach(action);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + this.individualWeight;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphNode other = (GraphNode) obj;
        if (this.individualWeight != other.individualWeight) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "GraphNode{" + "name=" + name + ", individualWeight=" + individualWeight + ", totalWeight=" + totalWeight + '}';
    }

}
