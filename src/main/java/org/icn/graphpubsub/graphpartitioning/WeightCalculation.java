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
import java.util.LinkedList;
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
    public static final String INTERMEDIATE_WEIGHT = WEIGHT_CALCULATION_PREFIX + "W_INT";
    public static final String FINAL_WEIGHT = WEIGHT_CALCULATION_PREFIX + "W_FIN";
    public static final String PARTITION = WEIGHT_CALCULATION_PREFIX + "PARTITION";

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
                parent.addChild(child);
            }
        }
    }

    public static HashMap<String, Collection<GraphNode>> readGraphPartitions(Reader partitionReader, HashMap<String, GraphNode> nodes) throws IOException {
        HashMap<String, Collection<GraphNode>> partitions = new HashMap<>();
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
                if (node.getValue(PARTITION) != null) {
                    LOG.log(Level.INFO, "Skipping line {0}, node already assigned to a partition: \"{1}\"", new Object[]{lineId, line});
                    continue;
                }
                node.putValue(PARTITION, partitionName);
                Collection<GraphNode> partitionNodes = partitions.get(partitionName);
                if (partitionNodes == null) {
                    partitions.put(partitionName, partitionNodes = new LinkedList<>());
                }
                partitionNodes.add(node);
            }
        }
        return partitions;
    }

    public static void calculateWeight(HashMap<String, GraphNode> nodes, HashMap<String, Collection<GraphNode>> partitions) {
        Collection<GraphNode> sortedNodes = GraphNodeAlgorithms.topologicalSort(nodes.values());
        sortedNodes.forEach(n -> System.out.printf("%s ", n.getValue(NAME)));
        System.out.println();
        System.out.println("=============================");

    }

}
