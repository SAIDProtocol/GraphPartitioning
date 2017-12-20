/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icn.graphpubsub.graphpartitioning;

import java.util.Collection;
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
        HashMap<Integer, GraphNode> nodes = new HashMap<>();
        nodes.put(2, new GraphNode("name", 2));
        nodes.put(3, new GraphNode("name", 3));
        nodes.put(5, new GraphNode("name", 5));
        nodes.put(7, new GraphNode("name", 7));
        nodes.put(8, new GraphNode("name", 8));
        nodes.put(9, new GraphNode("name", 9));
        nodes.put(10, new GraphNode("name", 10));
        nodes.put(11, new GraphNode("name", 11));

        nodes.get(3).addChild(nodes.get(8));
        nodes.get(3).addChild(nodes.get(10));
        nodes.get(5).addChild(nodes.get(11));
        nodes.get(7).addChild(nodes.get(8));
        nodes.get(7).addChild(nodes.get(11));
        nodes.get(8).addChild(nodes.get(9));
        nodes.get(11).addChild(nodes.get(2));
        nodes.get(11).addChild(nodes.get(9));
        nodes.get(11).addChild(nodes.get(10));

        Collection<GraphNode> sortResult = GraphNodeAlgorithms.topologicalSort(nodes.values());
//        Collections.reverse(sortResult);
        sortResult.forEach((graphNode) -> System.out.printf("%s ", graphNode.getValue("name")));
        System.out.println();
    }

}
