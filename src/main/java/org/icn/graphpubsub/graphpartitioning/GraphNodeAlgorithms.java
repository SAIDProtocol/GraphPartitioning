/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icn.graphpubsub.graphpartitioning;

import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author Jiachen Chen
 */
public class GraphNodeAlgorithms {

    public static HashSet<GraphNode> getReachableNodes(GraphNode root) {
        HashSet<GraphNode> results = new HashSet<>();
        LinkedList<GraphNode> todos = new LinkedList<>();

        todos.add(root);

        while (!todos.isEmpty()) {
            GraphNode todo = todos.removeFirst();
            if (results.contains(todo)) {
                continue;
            }
            results.add(todo);
            todo.forEachChildren(c -> todos.addLast(c));
        }
        return results;
    }

    public static void calculateTotalWeight(Iterable<GraphNode> nodes) {
        nodes.forEach(n -> {
            int weight = n.getIncomingWeight() + n.getIndividualWeight();
            getReachableNodes(n).forEach(r -> r.addTotalWeight(weight));
        });
    }

    public static class RPLoad {

        /**
         * The # of messages that will be unicasted to this RP
         */
        public int Input;
        
        /**
         * The # of messages that will be unicasted to other RPs from this RP
         */
        public int Output;
        /**
         * The # of messages this RP has to deal with (send multicast)
         */
        public int Weight;

        public RPLoad() {
            Input = Output = Weight = 0;
        }
    }

    public static RPLoad calculateRPLoad(HashSet<GraphNode> nodes) {
        RPLoad result = new RPLoad();
        nodes.forEach(node -> {
            result.Input += node.getIncomingWeight();
            node.forEachParent(p -> result.Input += nodes.contains(p) ? 0 : p.getTotalWeight());
            result.Output += node.getOutgoingWeight();
            node.forEachChildren(c -> result.Output += nodes.contains(c) ? 0 : node.getTotalWeight());
            result.Weight += node.getTotalWeight();
        });

        return null;
    }
}
