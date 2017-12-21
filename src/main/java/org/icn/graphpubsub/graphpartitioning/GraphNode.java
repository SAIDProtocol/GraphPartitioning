package org.icn.graphpubsub.graphpartitioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author Jiachen Chen
 */
public class GraphNode {

    private final HashMap<String, Object> data = new HashMap<>();

    public GraphNode() {
    }

    public GraphNode(String initialKey, Object initialValue) {
        data.put(initialKey, initialValue);
    }

    public GraphNode(Iterable<Map.Entry<String, Object>> initialValues) {
        for (Map.Entry<String, Object> initialValue : initialValues) {
            data.put(initialValue.getKey(), initialValue.getValue());
        }
    }

    public Object putValue(String key, Object value) {
        return data.put(key, value);
    }

    public Object getValue(String key) {
        return data.get(key);
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

    public void forEachChild(Consumer<? super GraphNode> action) {
        children.forEach(action);
    }

    public Iterator<GraphNode> getParents() {
        return parents.iterator();
    }
    
    
    public Iterator<GraphNode> getChildren() {
        return children.iterator();
    }
}
