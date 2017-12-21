/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icn.graphpubsub.graphpartitioning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jiachen
 */
public class WeightCalculation {

    private static final Logger LOG = Logger.getLogger(WeightCalculation.class.getName());

    public static final String WEIGHT_CALCULATION_PREFIX = WeightCalculation.class.getName() + "/";
    public static final String NAME = WEIGHT_CALCULATION_PREFIX + "NAME";
    public static final String ORIG_WEIGHT = WEIGHT_CALCULATION_PREFIX + "W_ORI";
    public static final String PARTITION_WEIGHT_OBJECT = WEIGHT_CALCULATION_PREFIX + "W_PART";

    public static class WeightObject {

        public String partition;
        public int intermediateWeight, finalWeight;

        public WeightObject(String partition) {
            this.partition = partition;
            intermediateWeight = finalWeight = 0;
        }
    }

    public static class PartitionObject {

        public int multicastCount;
        public HashMap<String, Integer> unicastCounts = new HashMap<>();
    }

    public static HashMap<String, GraphNode> readGraphNodes(Reader nodeReader) throws IOException {
        HashMap<String, GraphNode> nodes = new HashMap<>();
        String line;
        int lineId = 0;
        try (BufferedReader r = new BufferedReader(nodeReader)) {
            while ((line = r.readLine()) != null) {
                lineId++;
                String line2 = line.trim();
                if (line2.length() == 0) {
                    LOG.log(Level.INFO, "Skipping empty line {0}", lineId);
                    continue;
                }
                if (line2.startsWith("#")) {
                    LOG.log(Level.INFO, "Skipping comment line {0}: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                String[] parts = line2.split(" ");
                if (parts.length != 2) {
                    LOG.log(Level.INFO, "Skipping misformatted line {0}: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                String name = parts[0];
                int value;
                if (nodes.containsKey(name)) {
                    LOG.log(Level.INFO, "Skipping line {0} with duplicate name: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                try {
                    value = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    LOG.log(Level.INFO, "Skipping line {0} with error value: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                GraphNode n = new GraphNode(NAME, name);
                n.putValue(ORIG_WEIGHT, value);
                nodes.put(name, n);
            }
        }
        return nodes;
    }

    public static void readGraphLinks(Reader linkReader, HashMap<String, GraphNode> nodes) throws IOException {
        int lineId = 0;
        String line;
        try (BufferedReader r = new BufferedReader(linkReader)) {
            while ((line = r.readLine()) != null) {
                lineId++;
                String line2 = line.trim();
                if (line2.length() == 0) {
                    LOG.log(Level.INFO, "Skipping empty line {0}", lineId);
                    continue;
                }
                if (line2.startsWith("#")) {
                    LOG.log(Level.INFO, "Skipping comment line {0}: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                String[] parts = line2.split(" ");
                if (parts.length != 2) {
                    LOG.log(Level.INFO, "Skipping misformatted line {0}: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                String parentName = parts[0];
                String childName = parts[1];
                GraphNode parent = nodes.get(parentName);
                GraphNode child = nodes.get(childName);
                if (parent == null) {
                    LOG.log(Level.INFO, "Skipping line {0}, cannot find parent node: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                if (child == null) {
                    LOG.log(Level.INFO, "Skipping line {0}, cannot find child node: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                if (!parent.addChild(child)) {
                    LOG.log(Level.INFO, "Skipping line {0}, parent already has the child: \"{1}\"", new Object[]{lineId, line});
                }
            }
        }
    }

    public static HashMap<String, Collection<GraphNode>> readGraphPartitions(Reader partitionReader, HashMap<String, GraphNode> nodes, String partitionSuffix) throws IOException {
        HashMap<String, Collection<GraphNode>> partitions = new HashMap<>();
        String partitionString = PARTITION_WEIGHT_OBJECT + partitionSuffix;
        String line;
        int lineId = 0;
        try (BufferedReader r = new BufferedReader(partitionReader)) {
            while ((line = r.readLine()) != null) {
                lineId++;
                String line2 = line.trim();
                if (line2.length() == 0) {
                    LOG.log(Level.INFO, "Skipping empty line {0}", lineId);
                    continue;
                }
                if (line2.startsWith("#")) {
                    LOG.log(Level.INFO, "Skipping comment line {0}: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                String[] parts = line2.split(" ");
                if (parts.length != 2) {
                    LOG.log(Level.INFO, "Skipping misformatted line {0}: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                String nodeName = parts[0];
                String partitionName = parts[1];
                GraphNode node = nodes.get(nodeName);
                if (node == null) {
                    LOG.log(Level.INFO, "Skipping line {0}, cannot find node: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                if (node.getValue(partitionString) != null) {
                    LOG.log(Level.INFO, "Skipping line {0}, node already assigned to a partition: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                node.putValue(partitionString, new WeightObject(partitionName));
                Collection<GraphNode> partitionNodes = partitions.get(partitionName);
                if (partitionNodes == null) {
                    partitions.put(partitionName, partitionNodes = new LinkedList<>());
                }
                partitionNodes.add(node);
            }
        }
        return partitions;
    }

    public static HashMap<String, PartitionObject> calculateWeight(HashMap<String, GraphNode> nodes, String partitionSuffix) {
        Collection<GraphNode> sortedNodes = GraphNodeAlgorithms.topologicalSort(nodes.values());
        String partitionString = PARTITION_WEIGHT_OBJECT + partitionSuffix;
        HashMap<String, PartitionObject> partitionWeight = new HashMap<>();

        sortedNodes.forEach(n -> System.out.printf("%s ", n.getValue(NAME)));
        System.out.println();

        Consumer<String> nodeStatusPrinter = (str) -> {
            for (GraphNode value : nodes.values()) {
                WeightObject wo = (WeightObject) value.getValue(partitionString);
                System.out.printf("  %s I=%d, F=%d%n", value.getValue(NAME), wo.intermediateWeight, wo.finalWeight);
            }
        };

        for (GraphNode node : nodes.values()) {
            WeightObject obj = (WeightObject) node.getValue(partitionString);
            if (!partitionWeight.containsKey(obj.partition)) {
                partitionWeight.put(obj.partition, new PartitionObject());
            }
            obj.intermediateWeight = (Integer) node.getValue(ORIG_WEIGHT);
            obj.finalWeight = 0;
        }

        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "Init");
            nodeStatusPrinter.accept(null);
        }

        for (GraphNode node : sortedNodes) {
            WeightObject obj = (WeightObject) node.getValue(partitionString);
            int weightToPropagate = obj.intermediateWeight;
            obj.intermediateWeight = 0;
            PartitionObject fromPartiton = partitionWeight.get(obj.partition);

            // BFS in partition and add weightToPropagate to finalWeight
            // add weightToPropagate to intermediateWeight when crossing partition
            HashSet<GraphNode> traversed = new HashSet<>();
            LinkedList<GraphNode> todos = new LinkedList<>();
            todos.add(node);
            while (!todos.isEmpty()) {
                GraphNode todo = todos.removeFirst();
                if (traversed.contains(todo)) {
                    continue;
                }
                WeightObject traversingObj = (WeightObject) todo.getValue(partitionString);
                // move this line into the "same partition" block to count the unoptimized value.
                traversed.add(todo);
                String targetPartition = traversingObj.partition;
                if (targetPartition.equals(obj.partition)) { // same partition
                    traversingObj.finalWeight += weightToPropagate;
                    Iterator<GraphNode> children = todo.getChildren();
                    while (children.hasNext()) {
                        todos.add(children.next());
                    }
                } else { // different partition
                    traversingObj.intermediateWeight += weightToPropagate;
                    // add weightToPropagate to the unicast count from current partition
                    fromPartiton.unicastCounts.compute(targetPartition, (k, v) -> v == null ? weightToPropagate : (v + weightToPropagate));
                }
            }

            fromPartiton.multicastCount += obj.finalWeight;
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.INFO, "Propagated {0}, weight {1}", new Object[]{node.getValue(NAME), weightToPropagate});
                nodeStatusPrinter.accept(null);
            }
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "Final");
            nodeStatusPrinter.accept(null);
        }

        return partitionWeight;
    }

}
