package org.example.SequenceFinder;

import org.example.SequenceFinder.GeometricObjects.Box;

/**
 * Represents the spacial relations of the objects in the octree, namely that if node v has a directed edge to
 * node w, node v is either somewhere in front or above node w. <br>
 * This means the object described by object v needs to be removed before object w can be removed. <br>
 * <br>
 * The weight of the edges represents the cost of taking object v after object w. A metric to calculate weight could be
 * the height difference of both objects, or a constant value when ht e
 *
 * @param <T> the object that will be saved in this Graph
 */
public class Graph<T extends Box> {

    /**
     * The nodes of the graph
     */
    private T[] nodes;

    /**
     * the edges of all nodes represented by an adjacency matrix. <br>
     * Value 0 represent no existing edge between node v and w.
     */
    private double[][] edges;

    /**
     * the operating direction
     */
    private OperatingDirection opDir;

    public Graph(OperatingDirection opDir) {
        this.opDir = opDir;
    }
}
