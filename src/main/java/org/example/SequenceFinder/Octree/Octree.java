package org.example.SequenceFinder.Octree;

import org.example.SequenceFinder.GeometricObjects.Box;

/**
 * An Octree to store each object based on its position in the world. Used to improve performance for frustum culling
 * @param <T> a box shaped object.
 */
public class Octree<T extends Box> {

    /**
     * the maximum depth of the octree
     */
    private int maxDepth;

    /**
     * array holding all objects in the octree. <br>
     * The first dimension represents depth, the second and third dimension and fourth
     * represent the x,y,z index in the tree.
     *
//     * @see calcIndex
     */
    private T[][][][] nodes;

    /**
     * size of the world. Large enough to fit every object into it. Should be a multiple of 2
     */
    private int worldSize;

    /**
     * factor used to make the Octree loose. A value of > 1 looses the tree.
     */
    private double k;

    public Octree(int maxDepth, int worldSize, double k) {
        this.maxDepth = maxDepth;
        this.worldSize = worldSize;
        this.k = k;
    }
}
