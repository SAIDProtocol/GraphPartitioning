/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icn.graphpubsub.graphpartitioning;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jiachen Chen
 */
public class GraphNodeTest {

    public GraphNodeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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

    @Test
    public void test1() {
        HashMap<Character, GraphNode> nodes = new HashMap<>();
        int[] weights = new int[]{4, 2, 3, 8, 4, 16, 26, 2, 1};
        char[] linkFroms = new char[]{'a', 'a', 'a', 'b', 'c', 'c', 'c', 'd', 'e', 'h'};
        char[] linkTos = new char[]{'b', 'c', 'd', 'e', 'e', 'f', 'g', 'h', 'i', 'g'};
        for (int i = 0; i < weights.length; i++) {
            Character c = (char) ('a' + i);
            GraphNode gn = new GraphNode(c.toString(), weights[i], 0, 0);
            nodes.put(c, gn);
        }
        for (int i = 0; i < linkFroms.length; i++) {
            nodes.get(linkFroms[i]).addChild(nodes.get(linkTos[i]));
        }
        System.out.println("========Parents========");
        nodes.values().forEach(n -> {
            System.out.printf("%s%n", n);
            n.forEachParent(p -> System.out.printf("\t%s%n", p));
        });

        System.out.println("========Children========");
        nodes.values().forEach(n -> {
            System.out.printf("%s%n", n);
            n.forEachChildren(c -> System.out.printf("\t%s%n", c));
        });

        System.out.println("========Reachable========");
        nodes.values().forEach(n -> {
            System.out.printf("%s%n", n);
            GraphNodeAlgorithms.getReachableNodes(n).forEach(r -> System.out.printf("\t%s%n", r));
        });

        System.out.println("========TotalWeights========");
        GraphNodeAlgorithms.calculateTotalWeight(nodes.values());
        nodes.values().forEach(n -> {
            System.out.printf("%s%n", n);
        });
    }

}
