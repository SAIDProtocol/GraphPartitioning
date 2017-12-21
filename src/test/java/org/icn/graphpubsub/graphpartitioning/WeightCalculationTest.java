/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icn.graphpubsub.graphpartitioning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.icn.graphpubsub.graphpartitioning.WeightCalculation.*;

/**
 *
 * @author jiachen
 */
public class WeightCalculationTest {

    public WeightCalculationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // uncomment the following line to show the calculation detail
//        Logger.getLogger(WeightCalculation.class.getName()).setLevel(Level.ALL);;
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public HashMap<String, GraphNode> testGetGraph1() throws IOException {

        HashMap<String, GraphNode> nodes;
        try (ByteArrayOutputStream nodesOutput = new ByteArrayOutputStream()) {
            try (PrintStream ps = new PrintStream(nodesOutput)) {
                ps.print("v1 7\n"
                        + "v2 15\n"
                        + "v3 4\n"
                        + "v4 5\n"
                        + "v5 9\n"
                        + "v6 3\n"
                        + "v7 18\n"
                        + "v8 9\n"
                        + "v9 11\n"
                        + "v10 7\n"
                        + "v11 11\n"
                        + "v12 10\n"
                        + "v13 4\n"
                        + "v14 6\n"
                        + "v15 8\n"
                        + "v16 2\n"
                        + "v17 8\n"
                        + "v18 11\n"
                        + "v19 6 \n"
                        // error tests
                        + "  #\n"
                        + "#\n"
                        + "v19 8\n"
                        + "v21 a\n"
                        + "v19 6 a\n"
                        + "b\n"
                        // error tests ended
                        + "v20 8");
                ps.flush();
            }
            try (InputStreamReader nodeReader = new InputStreamReader(new ByteArrayInputStream(nodesOutput.toByteArray()))) {
                nodes = readGraphNodes(nodeReader);
            }
        }
        try (ByteArrayOutputStream linksOutput = new ByteArrayOutputStream()) {
            try (PrintStream ps = new PrintStream(linksOutput)) {
                ps.print("v1 v2\n"
                        + "v1 v3\n"
                        + "v1 v4\n"
                        + "v2 v3\n"
                        + "v3 v5\n"
                        + "v3 v6\n"
                        + "v4 v7\n"
                        + "v5 v6\n"
                        + "v5 v8\n"
                        + "v5 v9\n"
                        + "v10 v11\n"
                        + "v20 v19\n"
                        + "v20 v18\n"
                        + "v20 v17\n"
                        + "v18 v16\n"
                        + "v18 v15\n"
                        + "v17 v14\n"
                        + "v16 v15\n"
                        + "v15 v13\n"
                        + "v14 v12\n"
                        + "v14 v11\n"
                        + "v13 v12\n"
                        + "v11 v9\n"
                        + "v11 v8");
                ps.flush();
            }
            try (InputStreamReader linkReader = new InputStreamReader(new ByteArrayInputStream(linksOutput.toByteArray()))) {
                readGraphLinks(linkReader, nodes);
            }
        }
        return nodes;
    }

    public HashMap<String, Collection<GraphNode>> testGetGraph1Partition1(HashMap<String, GraphNode> nodes, String partitionSuffix) throws IOException {
        try (ByteArrayOutputStream partitionOutput = new ByteArrayOutputStream()) {
            try (PrintStream ps = new PrintStream(partitionOutput)) {
                ps.print("v6 0\n"
                        + "v7 1\n"
                        + "v8 1\n"
                        + "v9 0\n"
                        + "v10 0\n"
                        + "v20 0\n"
                        + "v12 1\n"
                        + "v11 1\n"
                        + "v14 1\n"
                        + "v13 0\n"
                        + "v16 0\n"
                        + "v1 0\n"
                        + "v15 1\n"
                        + "v2 1\n"
                        + "v18 0\n"
                        + "v3 1\n"
                        + "v17 0\n"
                        + "v4 1\n"
                        + "v5 1\n"
                        + "v19 1");
                ps.flush();
            }
            try (InputStreamReader linkReader = new InputStreamReader(new ByteArrayInputStream(partitionOutput.toByteArray()))) {
                return readGraphPartitions(linkReader, nodes, partitionSuffix);
            }
        }
    }

    public HashMap<String, Collection<GraphNode>> testGetGraph1Partition2(HashMap<String, GraphNode> nodes, String partitionSuffix) throws IOException {
        try (ByteArrayOutputStream partitionOutput = new ByteArrayOutputStream()) {
            try (PrintStream ps = new PrintStream(partitionOutput)) {
                ps.print("v6 1\n"
                        + "v7 0\n"
                        + "v8 0\n"
                        + "v9 0\n"
                        + "v10 0\n"
                        + "v20 0\n"
                        + "v12 1\n"
                        + "v11 1\n"
                        + "v14 0\n"
                        + "v13 1\n"
                        + "v16 1\n"
                        + "v1 0\n"
                        + "v15 0\n"
                        + "v2 0\n"
                        + "v18 1\n"
                        + "v3 1\n"
                        + "v17 0\n"
                        + "v4 1\n"
                        + "v5 0\n"
                        + "v19 0");
                ps.flush();
            }
            try (InputStreamReader linkReader = new InputStreamReader(new ByteArrayInputStream(partitionOutput.toByteArray()))) {
                return readGraphPartitions(linkReader, nodes, partitionSuffix);
            }
        }
    }

    @Test
    public void test1() throws IOException {
        HashMap<String, GraphNode> nodes = testGetGraph1();
        System.out.printf("Nodes:%d%n", nodes.size());
        nodes.values().forEach(n -> System.out.printf("Name=%s, Weight=%d%n", n.getValue(NAME), n.getValue(ORIG_WEIGHT)));
        System.out.println("=============================");
        AtomicInteger count = new AtomicInteger();
        nodes.values().forEach(n -> {
            n.forEachChild(c -> {
                System.out.printf("%s->%s%n", n.getValue(NAME), c.getValue(NAME));
                count.incrementAndGet();
            });
        });
        System.out.printf("Links:%d%n", count.get());
        System.out.println("=============================");
        HashMap<String, Collection<GraphNode>> partitions;
        partitions = testGetGraph1Partition2(nodes, "P1");
        System.out.printf("Partitions:%d%n", partitions.size());
        partitions.forEach((name, pNodes) -> {
            System.out.printf("partition \"%s\"%n", name);
            pNodes.forEach(n -> System.out.printf("  %s%n", n.getValue(NAME)));
        });
        System.out.println("=============================");
        HashMap<String, PartitionObject> partitionWeights = calculateWeight(nodes, "P1");
        partitionWeights.forEach((name, partition) -> {
            System.out.printf("Partition %s, multicast=%d, unicasts:%n", name, partition.multicastCount);
            partition.unicastCounts.forEach((target, unicastCount) -> {
                System.out.printf("  ->%s=%d%n", target, unicastCount);
            });
        });
    }

    @Test
    public void test2() {
        String partitionSuffix = "P1";
        String partitionString = PARTITION_WEIGHT_OBJECT + partitionSuffix;
        HashMap<String, GraphNode> nodes = new HashMap<>();

        GraphNode node;
        nodes.put("A", node = new GraphNode(NAME, "A"));
        node.putValue(ORIG_WEIGHT, 1);
        node.putValue(partitionString, new WeightObject("1"));
        nodes.put("B", node = new GraphNode(NAME, "B"));
        node.putValue(ORIG_WEIGHT, 10);
        node.putValue(partitionString, new WeightObject("2"));
        nodes.put("C", node = new GraphNode(NAME, "C"));
        node.putValue(ORIG_WEIGHT, 100);
        node.putValue(partitionString, new WeightObject("2"));
        nodes.put("D", node = new GraphNode(NAME, "D"));
        node.putValue(ORIG_WEIGHT, 1000);
        node.putValue(partitionString, new WeightObject("1"));
        nodes.put("E", node = new GraphNode(NAME, "E"));
        node.putValue(ORIG_WEIGHT, 10000);
        node.putValue(partitionString, new WeightObject("1"));
        nodes.get("A").addChild(nodes.get("B"));
        nodes.get("A").addChild(nodes.get("C"));
        nodes.get("A").addChild(nodes.get("D"));
        nodes.get("B").addChild(nodes.get("C"));
        nodes.get("D").addChild(nodes.get("E"));

        System.out.printf("Nodes:%d%n", nodes.size());
        nodes.values().forEach(n -> System.out.printf("Name=%s, Weight=%d, Partition=%s%n",
                n.getValue(NAME), n.getValue(ORIG_WEIGHT), ((WeightObject) n.getValue(partitionString)).partition));
        System.out.println("=============================");
        AtomicInteger count = new AtomicInteger();
        nodes.values().forEach(n -> {
            n.forEachChild(c -> {
                System.out.printf("%s->%s%n", n.getValue(NAME), c.getValue(NAME));
                count.incrementAndGet();
            });
        });
        System.out.printf("Links:%d%n", count.get());
        System.out.println("=============================");
        HashMap<String, PartitionObject> partitionWeights = calculateWeight(nodes, "P1");
        partitionWeights.forEach((name, partition) -> {
            System.out.printf("Partition %s, multicast=%d, unicasts:%n", name, partition.multicastCount);
            partition.unicastCounts.forEach((target, unicastCount) -> {
                System.out.printf("  ->%s=%d%n", target, unicastCount);
            });
        });
    }

//    @Test
//    public void test3() {
//        HashMap<String, Integer> values = new HashMap<>();
//        values.put("A", 234);
//        int r = 3;
//        BiFunction<? super String, ? super Integer, ? extends Integer> f = (k, v) -> v == null ? r : (v + r);
//        values.compute("A", f);
//        values.compute("B", f);
//        values.forEach((k, v) -> System.out.printf("%s:%d%n", k, v));
//        values.compute("A", f);
//        values.compute("B", f);
//        values.forEach((k, v) -> System.out.printf("%s:%d%n", k, v));
//    }
}
