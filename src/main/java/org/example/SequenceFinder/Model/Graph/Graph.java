package org.example.SequenceFinder.Model.Graph;

import org.example.SequenceFinder.OperatingDirection;

import java.util.*;

/**
 * Represents the spacial relations of the objects in the octree, namely that if node v has a directed edge to node w,
 * node v is either somewhere in front or above node w.
 * <p>
 * This means, the object described by object v needs to be removed, before object w can be removed.
 * <p>
 * Therefore, only nodes with no incoming edges can be removed. When removing a node, all of its outgoing edges are
 * removed as well.
 * <p>
 * The weight of the edges is calculated on-the-fly by the
 * {@linkplain org.example.SequenceFinder.Control.CostAssigner}.
 *
 * @param <T> the object that will be saved in this Graph
 */
public class Graph<T> {

    /**
     * the nodes of the graph
     */
    private final Set<GraphNode<T>> nodes;

    /**
     * nodes with no incoming edges are removable
     */
    private final Set<GraphNode<T>> removableNodes;

    /**
     * Used to know which OperatingDirection this graph represents.
     */
    private final Set<OperatingDirection> operatingDirections;

    /**
     * Creates a new directed Graph for the given {@linkplain OperatingDirection}s.
     * <p>
     * A graph represents the spacial relations of the objects in the octree, namely that if node v has a directed edge
     * to node w, node v is either somewhere in front (relative to the {@linkplain OperatingDirection}) or above node w.
     * This means, the object described by object v needs to be removed, before object w can be removed from the
     * given {@linkplain OperatingDirection}.
     * <p>
     * Therefore, only nodes with no incoming edges for at least one {@linkplain OperatingDirection} can be removed.
     * When removing a node, all of its outgoing edges are removed as well.
     * <p>
     * The weight of the edges is calculated on-the-fly by the
     * {@linkplain org.example.SequenceFinder.Control.CostAssigner}.
     *
     * @param operatingDirections the operating directions this graph will represent
     */
    public Graph(Set<OperatingDirection> operatingDirections) {
        if (operatingDirections == null) {
            throw new IllegalArgumentException("The operating directions must not be null!");
        }
        this.operatingDirections = operatingDirections;
        this.nodes = new HashSet<>();
        this.removableNodes = new HashSet<>();
    }

    /**
     * Create a new instance of the given graph
     * @param otherGraph the given graph
     */
    public Graph(Graph<T> otherGraph){
        this.operatingDirections = otherGraph.operatingDirections;
        this.nodes = otherGraph.nodes;
        this.removableNodes = otherGraph.removableNodes;
    }

    /**
     * Merge the set of graphs into a single graph.
     * <p>
     * Each node has multiple edges, one for each graph in the set.
     *
     * @param graphSet the set of graphs
     * @param <T>      the type of object that each graph contains. This type must be the same for all graphs
     * @return the merged graph
     */
    public static <T> Graph<T> merge(HashSet<Graph<T>> graphSet) {
        Set<GraphNode<T>> allNodes = new HashSet<>();
        Set<OperatingDirection> opDirs = new HashSet<>();
        Graph<T> finalGraph = new Graph<>(opDirs);

        // collect all nodes
        for (Graph<T> graph : graphSet) {
            allNodes.addAll(graph.getCopyOfNodes());
            opDirs.addAll(graph.getOperatingDirections());
        }

        // as each node represents an object (=content), and each graph represents a different operating direction,
        // collect the contents of all nodes. The nodes themselves, even if they represent the same object, are
        // different Java Objects, as they have different edges.
        //
        // important that contents is a set to avoid duplicates
        Set<T> contents = new HashSet<>();
        for (GraphNode<T> node : allNodes) {
            contents.add(node.getContent());
        }

        // create the nodes in the final graph and collect them in a map, to be able to access them by their content
        Map<T, GraphNode<T>> finalGraphNodes = new HashMap<>();
        for (T content : contents) {
            GraphNode<T> finalGraphNode = finalGraph.addNode(content);
            finalGraphNodes.put(content, finalGraphNode);
        }

        // add the edges to the final graph
        for (GraphNode<T> nodeFromOtherGraph : allNodes){
            T nodeContent = nodeFromOtherGraph.getContent();
            Collection<GraphNode.DirectedGraphEdge<T>> outgoingEdges = nodeFromOtherGraph.getOutgoingEdges();

            // because ALL nodes are handled, all edges are covered if for each node the outgoing edges are added to
            // the final graph.
            GraphNode<T> from = finalGraphNodes.get(nodeContent);
            for (GraphNode.DirectedGraphEdge<T> edge : outgoingEdges) {
                GraphNode<T> to = finalGraphNodes.get(edge.getTo().getContent());
                finalGraph.addDirectedEdge(from, to, edge.getOperatingDirection());
            }
        }

        return finalGraph;
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
     * Remove a node from the graph and all of its outgoing edges, if it has no incoming edges. All nodes that now have
     * no incoming edges for at least one of the given {@linkplain OperatingDirection} become removable.
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

        HashSet<GraphNode<T>> nowRemovable = node.removeAllEdges(this.operatingDirections);
        this.removableNodes.addAll(nowRemovable);
    }


    /**
     * Add a directed edge to the graph for the given {@linkplain OperatingDirection}.
     *
     * @param from               the node from which the edge starts
     * @param to                 the node to which the edge leads
     * @param operatingDirection the operating direction for which the edge is added
     */
    public void addDirectedEdge(GraphNode<T> from, GraphNode<T> to, OperatingDirection operatingDirection) {
        if (!this.nodes.contains(from)) {
            throw new IllegalGraphStateException("Node: " + from + " does not exist in the graph");

        } else if (!this.nodes.contains(to)) {
            throw new IllegalGraphStateException("Node: " + to + " does not exist in the graph");

        } else if (!this.operatingDirections.contains(operatingDirection)) {
            throw new IllegalGraphStateException("The graph does not represent the given operating direction");

        } else {
            from.addDirectedEdgeTo(to, operatingDirection);

            // the node stays removable, if it is removable from any of the operating directions
            if(this.removableNodes.contains(to)) {
                boolean removable = false;
                for (OperatingDirection direction : this.operatingDirections) {
                    if (!to.hasIncomingEdges(direction)) {
                        removable = true;
                    }
                }
                if (!removable) {
                    this.removableNodes.remove(to);
                }
            }
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
    public Collection<GraphNode<T>> getCopyOfRemovableNodes() {
        return new HashSet<>(this.removableNodes);
    }

    /**
     * Get a copy of the nodes
     *
     * @return a copy of the nodes
     */
    public Collection<GraphNode<T>> getCopyOfNodes() {
        return new HashSet<>(this.nodes);
    }

    /**
     * Get the graph's operating direction
     *
     * @return the graph's operating direction
     */
    public Set<OperatingDirection> getOperatingDirections() {
        return this.operatingDirections;
    }
}
