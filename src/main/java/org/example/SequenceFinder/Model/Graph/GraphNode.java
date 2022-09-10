package org.example.SequenceFinder.Model.Graph;

import org.example.SequenceFinder.OperatingDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


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
    private final HashSet<DirectedGraphEdge<T>> incomingEdges;

    /**
     * nodes with an incoming edge from this node
     */
    private final HashSet<DirectedGraphEdge<T>> outgoingEdges;

    GraphNode(T content) {
        this.content = content;
        this.incomingEdges = new HashSet<>();
        this.outgoingEdges = new HashSet<>();
    }

    @Override
    public String toString() {

        return "GraphNode{" +
                "content=" + content + ", " +
                "#incomingEdges=" + incomingEdges.size() + ", " +
                "#outgoingdges=" + outgoingEdges.size() +
                "}";
    }

    /**
     * Two graph nodes are equal, if they represent the same object, i.e. they have the same content
     *
     * @param o the other object
     * @return true if both object have the same content
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof GraphNode) {
            return this.content == ((GraphNode<?>) o).getContent();
        } else {
            return false;
        }
    }

    /**
     * Remove all incoming and outgoing edges from this node and return the nodes that, as a result of this, are now
     * removable. This method is called when the node is removed from the graph.
     * <p>
     * When a set of {@linkplain  OperatingDirection} is given, the nodes that are removable are those, who have no
     * incoming edges for at least one of the given directions.
     * <p>
     * Otherwise, the removable nodes are those, who have no incoming edges at all.
     *
     * @param opDirs the directions for which the removable nodes are calculated (can be null)
     * @return the nodes that are now removable
     */
    HashSet<GraphNode<T>> removeAllEdges(Set<OperatingDirection> opDirs) {

        // nodes with no incoming edges are removable
        HashSet<GraphNode<T>> removableNodes = new HashSet<>();

        for (DirectedGraphEdge<T> edge : this.outgoingEdges) {
            GraphNode<T> targetNode = edge.getTo();
            targetNode.removeDirectedEdgeFrom(this, null);

            if (opDirs == null) {
                if (!targetNode.hasIncomingEdges(null)) {
                    removableNodes.add(targetNode);
                }
            } else {
                for (OperatingDirection opDir : opDirs) {
                    if (!targetNode.hasIncomingEdges(opDir)) {
                        removableNodes.add(targetNode);
                    }
                }
            }
        }
        this.outgoingEdges.clear();

        for (DirectedGraphEdge<T> edge : this.incomingEdges) {
            edge.getFrom().removeDirectedEdgeTo(this, null);
        }
        this.incomingEdges.clear();

        return removableNodes;
    }

    /**
     * Add a directed edge from the given node to this node for the given {@linkplain OperatingDirection}..
     *
     * @param from  the start of the directed edge
     * @param opDir the operating direction (can be null)
     */
    void addDirectedEdgeFrom(GraphNode<T> from, OperatingDirection opDir) {
        if (from.outgoingEdges.stream()
                .map(DirectedGraphEdge::getTo)
                .anyMatch(this::equals)) {
            throw new IllegalGraphStateException("The node " + from + " already has an outgoing edge " +
                    "to " + this);
        }
        if (this.incomingEdges.stream()
                .map(DirectedGraphEdge::getFrom)
                .anyMatch(from::equals)) {
            throw new IllegalGraphStateException("The node " + this + " already has an incoming edge " +
                    "from " + from);
        }
        DirectedGraphEdge<T> edge = new DirectedGraphEdge<>(from, this, opDir);

        // add the edge to this node's incoming edges
        if (!this.incomingEdges.add(edge)) {
            throw new IllegalGraphStateException("The node " + from + " already has an incoming edge " +
                    "from " + this);
        }

        // add the edge to the other node's outgoing edges
        if (!from.outgoingEdges.add(edge)) {
            throw new IllegalGraphStateException("The node " + this + " already has an outgoing edge " +
                    "to " + from);
        }
    }

    /**
     * Add a directed edge to the given node from this node for the given {@linkplain OperatingDirection}.
     *
     * @param to    the end of the directed edge
     * @param opDir the operating direction (can be null)
     */
    void addDirectedEdgeTo(GraphNode<T> to, OperatingDirection opDir) {
        if (this.outgoingEdges.stream()
                .filter(edge -> edge.getOperatingDirection().equals(opDir))
                .map(DirectedGraphEdge::getTo)
                .anyMatch(to::equals)) {
            throw new IllegalGraphStateException("The node " + this + " already has an outgoing edge for " +
                    "OperatingDirection " + opDir + " to " + to);
        }
        if (to.incomingEdges.stream()
                .filter(edge -> edge.getOperatingDirection().equals(opDir))
                .map(DirectedGraphEdge::getFrom)
                .anyMatch(this::equals)) {
            throw new IllegalGraphStateException("The node " + to + " already has an incoming edge for " +
                    "OperatingDirection " + opDir + " from " + this);
        }

        DirectedGraphEdge<T> edge = new DirectedGraphEdge<>(this, to, opDir);

        // add the edge to the other node's outgoing edges
        this.outgoingEdges.add(edge);

        // add the edge to this node's incoming edges
        to.incomingEdges.add(edge);
    }

    /**
     * Remove the directed edge from the given node to this node for the given {@linkplain OperatingDirection}.
     *
     * @param from  the start of the directed edge
     * @param opDir the operating direction (can be null)
     */
    void removeDirectedEdgeFrom(GraphNode<T> from, OperatingDirection opDir) {
        boolean removed = false;

        // remove ALL directed edges from the given node to this node if no OperatingDirection is given
        if (opDir == null) {
            for (DirectedGraphEdge<T> edge : from.outgoingEdges) {
                if (edge.getTo().equals(this)) {
                    removed = from.outgoingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + from + " does not have an outgoing edge " +
                        "to " + this);
            }
            removed = false;
            for (DirectedGraphEdge<T> edge : this.incomingEdges) {
                if (edge.getFrom().equals(from)) {
                    removed = this.incomingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + this + " does not have an incoming edge " +
                        "from " + from);
            }

            // remove ONLY the directed edge with the given OperatingDirection from the given node to this node
        } else {
            for (DirectedGraphEdge<T> edge : from.outgoingEdges) {
                if (edge.getTo().equals(this) && edge.getOperatingDirection().equals(opDir)) {
                    removed = from.outgoingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + from + " does not have an outgoing edge " +
                        "to " + this + " for the given operating direction " + opDir);
            }
            for (DirectedGraphEdge<T> edge : this.incomingEdges) {
                if (edge.getFrom().equals(from) && edge.getOperatingDirection().equals(opDir)) {
                    removed = this.incomingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + this + " does not have an incoming edge " +
                        "from " + from + " for the given operating direction " + opDir);
            }
        }
    }

    /**
     * Remove the directed edge to the given node from this node for the given {@linkplain OperatingDirection}.
     *
     * @param to    the end of the directed edge
     * @param opDir the operating direction (can be null)
     */
    void removeDirectedEdgeTo(GraphNode<T> to, OperatingDirection opDir) {
        boolean removed = false;

        // remove ALL directed edges from the given node to this node if no OperatingDirection is given
        if (opDir == null) {
            for (DirectedGraphEdge<T> edge : this.outgoingEdges) {
                if (edge.getTo().equals(to)) {
                    removed = this.outgoingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + this + " does not have an outgoing edge " +
                        "to " + to);
            }
            for (DirectedGraphEdge<T> edge : to.incomingEdges) {
                if (edge.getFrom().equals(this)) {
                    removed = to.incomingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + to + " does not have an incoming edge " +
                        "from " + this);
            }

            // remove ONLY the directed edge with the given OperatingDirection from the given node to this node
        } else {
            for (DirectedGraphEdge<T> edge : this.outgoingEdges) {
                if (edge.getTo().equals(to) && edge.getOperatingDirection().equals(opDir)) {
                    removed = this.outgoingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + this + " does not have an outgoing edge " +
                        "to " + to + " for the given operating direction " + opDir);
            }
            for (DirectedGraphEdge<T> edge : to.incomingEdges) {
                if (edge.getFrom().equals(this) && edge.getOperatingDirection().equals(opDir)) {
                    removed = to.incomingEdges.remove(edge);
                }
            }
            if (!removed) {
                throw new IllegalGraphStateException("The node " + to + " does not have an incoming edge " +
                        "from " + this + " for the given operating direction " + opDir);
            }
        }
    }

    /**
     * Check if this node has incoming edges for the given {@linkplain OperatingDirection}. If no
     * {@linkplain OperatingDirection} is given, check if this node has any incoming edges.
     *
     * @param opDir the operating direction (can be null)
     * @return true if this node has incoming edges
     */
    boolean hasIncomingEdges(OperatingDirection opDir) {
        // check if the node has incoming edges of no OperatingDirection is given
        if (opDir == null) {
            return !this.incomingEdges.isEmpty();

            // check if the node has any incoming edges for the given OperatingDirection
        } else {
            return this.incomingEdges.stream()
                    .map(DirectedGraphEdge::getOperatingDirection)
                    .anyMatch(opDir::equals);
        }
    }

    /**
     * Check if this node has outgoing edges for the given {@linkplain OperatingDirection}. If no
     * {@linkplain OperatingDirection} is given, check if this node has any outgoing edges.
     *
     * @param opDir the operating direction (can be null)
     * @return true if this node has incoming edges
     */
    boolean hasOutgoingEdges(OperatingDirection opDir) {
        // check if the node has outgoing edges of no OperatingDirection is given
        if (opDir == null) {
            return !this.outgoingEdges.isEmpty();

            // check if the node has any outgoing edges for the given OperatingDirection
        } else {
            return this.outgoingEdges.stream()
                    .map(DirectedGraphEdge::getOperatingDirection)
                    .anyMatch(opDir::equals);
        }
    }

    public T getContent() {
        return content;
    }

    /**
     * Get a copy of the nodes that have an outgoing edge TO this node
     *
     * @return a copy of the nodes that have an outgoing edge to this node
     */
    public Collection<GraphNode<T>> getCopyOfIncomingNodes() {
        return incomingEdges.stream()
                .map(DirectedGraphEdge::getFrom)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get a copy of the nodes that have an incoming edge FROM this node
     *
     * @return a copy of the nodes that have an incoming edge from this node
     */
    public Collection<GraphNode<T>> getCopyOfOutgoingNodes() {
        return outgoingEdges.stream()
                .map(DirectedGraphEdge::getTo)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get a copy of the nodes that have an outgoing edge with the given {@linkplain OperatingDirection} TO this node
     *
     * @return a copy of the nodes that have an outgoing edge to this node
     */
    public Collection<GraphNode<T>> getCopyOfIncomingNodes(OperatingDirection opDir) {
        return incomingEdges.stream()
                .filter(edge -> edge.getOperatingDirection().equals(opDir))
                .map(DirectedGraphEdge::getFrom)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get a copy of the nodes that have an incoming edge with the given {@linkplain OperatingDirection} FROM this node
     *
     * @return a copy of the nodes that have an incoming edge from this node
     */
    public Collection<GraphNode<T>> getCopyOfOutgoingNodes(OperatingDirection opDir) {
        return outgoingEdges.stream()
                .filter(edge -> edge.getOperatingDirection().equals(opDir))
                .map(DirectedGraphEdge::getTo)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Check if the node is removable from the given {@linkplain OperatingDirection}.
     * <p>
     * A node is removable from the given direction, if it has no incoming edges from this direction.
     *
     * @param opDir the operating direction
     * @return true if the node is removable from the given operating direction.
     */
    boolean isRemovable(OperatingDirection opDir) {
        return this.incomingEdges.stream()
                .noneMatch(edge -> edge.getOperatingDirection().equals(opDir));
    }

    /**
     * Check if the node is removable from any of the give {@linkplain OperatingDirection}s.
     * <p>
     * A node is removable from a direction, if it has no incoming edges from this direction.
     *
     * @param opDirs the OperatingDirections
     * @return true if the node is removable from any of the given operating directions.
     */
    boolean isRemovable(Set<OperatingDirection> opDirs) {
        return opDirs.stream()
                .anyMatch(this::isRemovable);
    }

    /**
     * Get the outgoing edges of this node
     *
     * @return the outgoing edges of this node
     */
    Collection<DirectedGraphEdge<T>> getOutgoingEdges() {
        return this.outgoingEdges;
    }


    //
    //-----------------------------------------------------------------------------------------------------------------
    //


    /**
     * An edge in the {@linkplain Graph}.
     *
     * @param <T> the type of object that is stored in the nodes of the graph
     */
    static class DirectedGraphEdge<T> {

        /**
         * the node where the edge originates
         */
        GraphNode<T> from;
        /**
         * the node where the edge ends
         */
        GraphNode<T> to;

        /**
         * the {@linkplain OperatingDirection} the edge corresponds to
         */
        OperatingDirection operatingDirection;

        /**
         * Creates a new edge from the given nodes.
         *
         * @param from the node where the edge starts
         * @param to   the node where the edge ends
         */
        DirectedGraphEdge(GraphNode<T> from, GraphNode<T> to, OperatingDirection operatingDirection) {
            if(operatingDirection == null){
                throw new IllegalArgumentException("The operating direction must not be null, the edge needs to " +
                        "represent a direction");
            }
            this.from = from;
            this.to = to;
            this.operatingDirection = operatingDirection;
        }

        OperatingDirection getOperatingDirection() {
            return operatingDirection;
        }

        GraphNode<T> getFrom() {
            return this.from;
        }

        GraphNode<T> getTo() {
            return to;
        }
    }

}
