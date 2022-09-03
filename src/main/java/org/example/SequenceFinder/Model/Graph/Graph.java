package org.example.SequenceFinder.Model.Graph;

import org.example.SequenceFinder.OperatingDirection;

import java.util.HashSet;

/**
 * Represents the spacial relations of the objects in the octree, namely that if node v has a directed edge to
 * node w, node v is either somewhere in front or above node w. <br>
 * This means, the object described by object v needs to be removed, before object w can be removed. <br>
 * Therefore, only nodes with no incoming edges can be removed. When removing a node, all of its outgoing edges are
 * removed as well. <br>
 * <br>
 * The weight of the edges is calculated on-the-fly by the {@linkplain org.example.SequenceFinder.Control.CostAssigner}.
 *
 * @param <T> the object that will be saved in this Graph
 */
public class Graph<T> {

    /**
     * the nodes of the graph
     */
    private final HashSet<GraphNode<T>> nodes;

    /**
     * nodes with no incoming edges are removable
     */
    private final HashSet<GraphNode<T>> removableNodes;

    /**
     * the operating direction
     */
    private final OperatingDirection opDir;

    public Graph(OperatingDirection opDir) {
        this.opDir = opDir;
        this.nodes = new HashSet<>();
        this.removableNodes = new HashSet<>();
    }

    /**
     * Create a new node in the graph.
     *
     * @param object the object that will be represented by the created node
     * @return the constructed node
     */
    public GraphNode<T> addNode(T object) {
        GraphNode<T> node = new GraphNode<>(object);

        if (!this.nodes.add(node)) {
            throw new IllegalGraphStateException("The node was not added to the graph");
        }

        if (!this.removableNodes.add(node)) {
            throw new IllegalGraphStateException("The node was not added to the removable nodes");
        }

        return node;
    }

    /**
     * Remove a node from the graph, if it has no incoming edges. All nodes that now have no incoming edges become
     * removable.
     *
     * @param node the node that will be removed
     */
    public void removeNode(GraphNode<T> node) {
        if (!this.nodes.remove(node)) {
            throw new IllegalGraphStateException("Node: " + node + " does not exist in the graph");

        }
        if (!this.removableNodes.remove(node)) {
            throw new IllegalGraphStateException("Node: " + node + " has incoming edges and cannot be removed");
        }

        HashSet<GraphNode<T>> nowRemovable = node.removeAllEdges();
        this.removableNodes.addAll(nowRemovable);
    }

    /**
     * Add a directed edge to the graph.
     *
     * @param from the node from which the edge starts
     * @param to   the node to which the edge leads
     */
    public void addDirectedEdge(GraphNode<T> from, GraphNode<T> to) {
        if (!this.nodes.contains(from)) {
            throw new IllegalGraphStateException("Node: " + from + " does not exist in the graph");

        } else if (!this.nodes.contains(to)) {
            throw new IllegalGraphStateException("Node: " + to + " does not exist in the graph");

        } else {
            from.addDirectedEdgeTo(to);
            this.removableNodes.remove(to);
        }
    }

    /**
     * Check if a node in the graph is removable
     *
     * @param node the node
     * @return true if the node is removable, false otherwise
     */
    public boolean isRemovable(GraphNode<T> node) {
        return this.removableNodes.contains(node);
    }

    /**
     * Get a copy of the removable nodes
     *
     * @return a copy of the removable nodes
     */
    public HashSet<GraphNode<T>> getCopyOfRemovableNodes() {
        return new HashSet<>(this.removableNodes);
    }

    /**
     * Get a copy of the nodes
     *
     * @return a copy of the nodes
     */
    public HashSet<GraphNode<T>> getCopyOfNodes() {
        return new HashSet<>(this.nodes);
    }
}
