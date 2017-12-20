/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icn.graphpubsub.graphpartitioning;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Jiachen Chen
 */
public class GraphNodeAlgorithms {

//    private static final String GRAPH_DATA_PREFIX = GraphNodeAlgorithms.class.getName();

    /**
     * Output a list, sorted based on the graph.
     *
     * In the sorted list, the parent would always come before its children.
     *
     * @param allNodes all nodes in a graph.
     * @return sorted nodes output.
     * @throws IllegalArgumentException when the input graph is not a DAG
     * (contains circles)
     */
    public static Collection<GraphNode> topologicalSort(Iterable<GraphNode> allNodes) {
        LinkedList<GraphNode> ret = new LinkedList<>();
        // true if permanently marked
        // false if temporarily marked
        HashMap<GraphNode, Boolean> marks = new HashMap<>();

        for (GraphNode graphNode : allNodes) {
            if (!marks.containsKey(graphNode)) {
                if (!topologicalSortVisit(graphNode, marks, ret)) {
                    // not a DAG
                    throw new IllegalArgumentException("The input graph is not a DAG");
                }
            }
        }
        return ret;
    }

    // returns false if the graph is not a DAG.
    private static boolean topologicalSortVisit(GraphNode n,
            HashMap<GraphNode, Boolean> marks,
            LinkedList<GraphNode> sortResult) {
        Boolean mark = marks.get(n);
        if (mark != null) {
            // if n has a temporary mark, then stop (not a DAG)
            // if n has a permanent mark, then return
            return mark;
        }
        // mark n temporarily
        marks.put(n, false);
        // for each node m with an edge from n to m do
        Iterator<GraphNode> it = n.getChildren();
        while (it.hasNext()) {
            GraphNode m = it.next();
            // visit(m), if visit says it is not a DAG, stop
            if (!topologicalSortVisit(m, marks, sortResult)) {
                return false;
            }
        }
        // mark n permanently
        marks.put(n, true);
        // add n to head of L
        sortResult.addFirst(n);
        return true;

    }

    /**
     * Find all the reachable nodes from root with BFS.
     * 
     * @param root the root node to start with.
     * @return the reachable nodes.
     */
    public static HashSet<GraphNode> breathFirstSearch(GraphNode root) {
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

}
