package org.example.SequenceFinder.Control;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.Model.Graph.GraphNode;
import org.example.SequenceFinder.Model.Octree.Frustum;
import org.example.SequenceFinder.Model.Octree.LooseOctree;
import org.example.SequenceFinder.OperatingDirection;

import java.util.*;

/**
 * Controller to calculate which Boxes are removable in the stack of boxes.
 * <p>
 * It creates a single or multiple Graphs using frustum culling, where nodes with no incoming edges represent the
 * removable Boxes.
 *
 * @see Graph
 */
public class RemovableCalculator<T extends AABB> {

    /**
     * the octree where the boxes are stored
     */
    LooseOctree<T> octree;
    /**
     * the operating directions from where the boxes can be placed or removed
     */
    Set<OperatingDirection> opDirs;

    /**
     * Controller to calculate which Boxes are removable in the stack of boxes.
     * <p>
     * It creates a collection of {@linkplain Graph}s using frustum culling, where graph nodes with no incoming edges
     * represent the removable boxes.
     *
     * @param octree              the octree where the boxes are stored
     * @param operatingDirections the set of operating directions from where the boxes can be placed or removed
     * @see Graph
     */
    public RemovableCalculator(LooseOctree<T> octree, Set<OperatingDirection> operatingDirections) {
        this.octree = octree;
        this.opDirs = operatingDirections;
        if (operatingDirections.isEmpty()) {
            throw new IllegalArgumentException("The RemovableCalculator needs at least one OperatingDirection!");
        }
    }

    /**
     * Creates a map of graphs, one for each previously specified operating direction.
     * <p>
     * If the operating directions can change at any desired time, only a single graph is created. The map then maps all
     * operating directions to this single graph.
     *
     * @param opDirCanChange whether the operating direction can change. This means that is allowed to remove an item
     *                       from one direction, and the next one from another direction, i.e. remove BoxA from the left
     *                       direction and then remove BoxB from the right direction.
     * @return the collection of graphs
     */
    public Map<OperatingDirection, Graph<T>> createGraphs(boolean opDirCanChange) {
        HashSet<Graph<T>> graphSet = new HashSet<>();
        HashMap<OperatingDirection, Graph<T>> graphMap = new HashMap<>();

        for (OperatingDirection operatingDirection : opDirs) {
            Graph<T> createdGraph = createGraph(operatingDirection);
            graphSet.add(createdGraph);
            graphMap.put(operatingDirection, createdGraph);
        }

        if (opDirCanChange) {
            // map all operating directions to the same graph, because it is allowed to change the operating direction
            Graph<T> mergedGraph = Graph.merge(graphSet);
            for (OperatingDirection operatingDirection : opDirs) {
                graphMap.put(operatingDirection, mergedGraph);
            }
        }
        return graphMap;
    }


    /**
     * Creates a graph using frustum culling, where all nodes with no incoming edges represent removable Boxes in the
     * stack of boxes
     *
     * @param opDir the direction from where the Boxes can be removed.
     * @return the graph as described above
     */
    private Graph<T> createGraph(OperatingDirection opDir) {
        Collection<T> allObjects = octree.getAllObjects();

        Set<OperatingDirection> opDirSet = new HashSet<>();
        opDirSet.add(opDir);
        Graph<T> graph = new Graph<>(opDirSet);
        HashMap<T, GraphNode<T>> nodeMap = new HashMap<>();

        // generate all nodes to be able to add edges in the next for-loop
        for (T object : allObjects) {
            GraphNode<T> node = graph.addNode(object);
            nodeMap.put(object, node);
        }

        for (T object : allObjects) {
            // find all objects that are on top of this object
            Frustum topFrustum = createFrustum(object, OperatingDirection.TOP);
            Collection<T> objectsToRemove = octree.cullAgainst(topFrustum);

            // FIXME: problem when opDir == TOP? Maybe double edges are created? If so is it a problem or are
            //  duplicate edges removed when a node is removed?
            // find all objects that are in front of the object (relative to the given operating direction).
            Frustum inFrontFrustum = createFrustum(object, opDir);
            objectsToRemove.addAll(octree.cullAgainst(inFrontFrustum));

            // when culling the octree the object itself is also returned, so remove it.
            //  (the object and the frustum share a common plane, therefore the objects intersects the frustum)
            objectsToRemove.remove(object);

            // add an edge in the graph from each object that must be removed to the current object
            GraphNode<T> to = nodeMap.get(object);
            for (T objectToRemove : objectsToRemove) {
                GraphNode<T> from = nodeMap.get(objectToRemove);
                graph.addDirectedEdge(from, to, opDir);
            }
        }
        return graph;
    }

    /**
     * Creates a new  frustum from the AABB in direction of the operating direction.
     * <p>
     * The frustum is used to perform frustum culling onto the octree and calculate which AABB are in front or above the
     * given aabb<br> For example: when the operating direction is 'left', a frustum is created from the left side of
     * the aabb pointing towards the left direction.
     *
     * @param aabb  the aabb for which the frustum will be created
     * @param opDir the operating direction
     * @return a frustum pointing from the aabb towards the operating direction
     */
    private Frustum createFrustum(AABB aabb, OperatingDirection opDir) {
        // the back of the frustum is the side of the aabb that is facing towards the operating direction
        Plane back = aabb.getSide(opDir, false);

        // the front of the frustum is the border of the world
        Plane front = octree.getWorldAABB().getSide(opDir, true);

        Plane left = aabb.getSide(opDir.getLeft(), true);
        Plane right = aabb.getSide(opDir.getRight(), true);
        Plane top = aabb.getSide(opDir.getTop(), true);
        Plane bottom = aabb.getSide(opDir.getBottom(), true);

        return new Frustum(front, back, left, right, top, bottom);
    }
}
