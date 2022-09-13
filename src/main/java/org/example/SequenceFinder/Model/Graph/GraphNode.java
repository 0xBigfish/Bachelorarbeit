package org.example.SequenceFinder.Model.Graph;

import java.util.HashSet;

/**
 * A node in the {@linkplain Graph}
 *
 * @param <T> the type of object that is stored in this node
 */
public class GraphNode<T> {
    /**
     * the object that is represented by this node
     */
    private final T content;

    /**
     * nodes with an outgoing edge to this node
     */
    private final HashSet<GraphNode<T>> incomingNodes;

    /**
     * nodes with an incoming edge from this node
     */
    private final HashSet<GraphNode<T>> outgoingNodes;

    protected GraphNode(T content) {
        this.content = content;
        this.incomingNodes = new HashSet<>();
        this.outgoingNodes = new HashSet<>();
    }

    /**
     * Remove all incoming and outgoing edges from this node and return the nodes that, as a result of this, are now
     * removable. This method is called when the node is removed from the graph.
     * <br>
     *
     * @return the nodes that are now removable
     */
    protected HashSet<GraphNode<T>> removeAllEdges() {

        // nodes with no incoming edges are removable
        HashSet<GraphNode<T>> removableNodes = new HashSet<>();

        for (GraphNode<T> node : this.outgoingNodes) {
            node.removeIncomingNode(this);
            if (!node.hasIncomingEdges()) {
                removableNodes.add(node);
            }
        }
        this.outgoingNodes.clear();

        for (GraphNode<T> node : this.incomingNodes) {
            node.removeOutgoingNode(this);
        }
        this.incomingNodes.clear();

        return removableNodes;
    }

    /**
     * Add a directed edge from the given node to this node
     *
     * @param from the start of the directed edge
     */
    protected void addDirectedEdgeFrom(GraphNode<T> from) {
        from.addOutgoingNode(this);
        this.incomingNodes.add(from);
    }

    /**
     * Add a directed edge to the given node from this node
     *
     * @param to the end of the directed edge
     */
    protected void addDirectedEdgeTo(GraphNode<T> to) {
        to.addIncomingNode(this);
        this.outgoingNodes.add(to);
    }

    /**
     * Remove the directed edge from the given node to this node
     *
     * @param from the start of the directed edge
     */
    protected void removeDirectedEdgeFrom(GraphNode<T> from) {
        from.removeOutgoingNode(this);
        this.incomingNodes.remove(from);
    }

    /**
     * Remove the directed edge to the given node from this node
     *
     * @param to the end of the directed edge
     */
    protected void removeDirectedEdgeTo(GraphNode<T> to) {
        to.removeIncomingNode(this);
        this.outgoingNodes.remove(to);
    }

    /**
     * Check if this node has incoming edges
     *
     * @return true if this node has incoming edges
     */
    boolean hasIncomingEdges() {
        return !this.incomingNodes.isEmpty();
    }

    /**
     * Check if this node has outgoing edges
     *
     * @return true if this node has outgoing edges
     */
    boolean hasOutgoingEdges() {
        return !this.outgoingNodes.isEmpty();
    }

    /**
     * Add a node to the list of nodes which have an outgoing edge to this node
     *
     * @param from a node that has an edge, that is incoming to this node
     */
    private void addIncomingNode(GraphNode<T> from) {
        if (!this.incomingNodes.add(from)) {
            throw new IllegalGraphStateException("The node " + from + " is already in the incoming nodes of " + this);
        }
    }

    /**
     * Add a node to the list of nodes which have an incoming edge from this node
     *
     * @param to a node that has an incoming edge from this node
     */
    private void addOutgoingNode(GraphNode<T> to) {
        if (!this.outgoingNodes.add(to)) {
            throw new IllegalGraphStateException("The node " + to + " is already in the outgoing nodes of " + this);
        }
    }

    /**
     * Remove a node from the list of nodes which have an outgoing edge to this node
     *
     * @param from a node that has an outgoing edge to this node
     */
    private void removeIncomingNode(GraphNode<T> from) {
        if (!this.incomingNodes.remove(from)) {
            throw new IllegalGraphStateException("The node " + from + " is not in the incoming nodes of " + this);
        }
    }

    /**
     * Remove a node from the list of nodes which have an incoming edge from this node
     *
     * @param to a node that has an incoming edge from this node
     */
    private void removeOutgoingNode(GraphNode<T> to) {
        if (!this.outgoingNodes.remove(to)) {
            throw new IllegalGraphStateException("The node " + to + " is not in the outgoing nodes of " + this);
        }
    }

    @Override
    public String toString() {

        return "GraphNode{" +
                "content=" + content + ", " +
                "#incomingEdges=" + incomingNodes.size() + ", " +
                "#outgoingdges=" + outgoingNodes.size() +
                "}";
    }


    public T getContent() {
        return content;
    }

    /**
     * Get a copy of the nodes that have an outgoing edge TO this node
     *
     * @return a copy of the nodes that have an outgoing edge to this node
     */
    public HashSet<GraphNode<T>> getCopyOfIncomingNodes() {
        return new HashSet<>(incomingNodes);
    }

    /**
     * Get a copy of the nodes that have an incoming edge FROM this node
     *
     * @return a copy of the nodes that have an incoming edge from this node
     */
    public HashSet<GraphNode<T>> getCopyOfOutgoingNodes() {
        return new HashSet<>(outgoingNodes);
    }
}
