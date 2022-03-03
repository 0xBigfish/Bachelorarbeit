package org.example.SequenceFinder.Octree;

import org.example.SequenceFinder.GeometricObjects.Box;
import org.example.SequenceFinder.GeometricObjects.Point;

import java.util.ArrayList;

/**
 * An Octree to store each object based on its position in the world. Used to improve performance for frustum culling
 *
 * @param <T> a box shaped object.
 */
public class Octree<T extends Box> {

    /**
     * the maximum depth of the octree
     */
    private final int maxDepth;

    /**
     * array holding all objects in the octree. <br>
     * The first dimension represents depth, the second and third dimension and fourth
     * represent the x,y,z index in the tree. <br>
     * <b>Note:</b> generics can't be instantiated as arrays, so an ArrayList is used with offsets representing the
     * depth and indices.
     * @see Octree#calcIndex(Box)
     */
    private ArrayList<T> nodes;

    /**
     * size of the world. Large enough to fit every object into it. Should be a multiple of 2
     */
    private final int worldSize;

    /**
     * factor used to make the Octree loose. A value of > 1 loosens the tree.
     */
    private final double k = 2;

    public Octree(int maxDepth, int worldSize) {
        this.maxDepth = maxDepth;
        this.worldSize = worldSize;

        int numOfNodes = 0;
        for(int i = 1; i <= maxDepth; i++){
            // worldSize / boundingCubeSpacing() = number of indices on this depth
            // multiply by 3 because there are x, y and z indices
            numOfNodes = numOfNodes + 3 * worldSize / boundingCubeSpacing(i);
        }
        nodes = new ArrayList<>(numOfNodes);
    }

    /**
     * Length of the loosened bounding cubes which recursively divide the world in 8 cubes
     *
     * @param depth the depth
     * @return the length of the bounding cubes at the given depth
     */
    public double boundingCubeLength(int depth){
        if (depth > maxDepth) {
            throw new IllegalArgumentException("Depth is too deep! Depth value: " + depth + ", maxDepth: " + maxDepth);
        }
        return k * worldSize / Math.pow(2, depth);
    }

    /**
     * Spacing of the cubes' centers. The cubes recursively divide the world in 8 cubes
     *
     * @param depth the depth
     * @return the spacing between the cube centers at the given depth
     */
    public int boundingCubeSpacing(int depth) {
        return worldSize / (2 ^ depth);
    }

    /**
     * Calculates at which depth in the object will fit in the tree , based on the object's radius
     *
     * @param radius the radius of the object
     * @return the depth at which the object will be placed
     */
    public int calcDepth(double radius) {
        // TODO: test if this method works correctly
        return Math.min(maxDepth, (int) Math.floor(log2(worldSize / radius)));
    }

    /**
     * Calculates the x,y,z indices of an object at which it will be stored in the octree.
     *
     * @param t the object
     * @return the indices wrapped in an Point object. The Point is just a wrapper to store the values
     */
    public Point calcIndex(T t) {
        double radius = t.calcRadius();
        int x = (int) Math.floor((t.calcCenter().x + (double) worldSize / 2) /
                boundingCubeSpacing(calcDepth(radius)));
        int y = (int) Math.floor((t.calcCenter().y + (double) worldSize / 2) /
                boundingCubeSpacing(calcDepth(radius)));
        int z = (int) Math.floor((t.calcCenter().z + (double) worldSize / 2) /
                boundingCubeSpacing(calcDepth(radius)));

        return new Point(x, y, z);
    }

    /**
     * Inserts an object into the octree.
     *
     * @param objectToInsert the object that will be inserted
     * @return returns true if inserted successfully
     */
    public boolean insertObject(T objectToInsert) {
        Point index = calcIndex(objectToInsert);
        int depth = calcDepth(objectToInsert.calcRadius());
        int indexX = (int) index.x;
        int indexY = (int) index.y;
        int indexZ = (int) index.z;


        int offset = 0;
        for(int i = 1; i < depth; i++){
            offset += numOfNodesAtDepth(i);
        }
        // FIXME: collision when adding indices: 2 + 2 + 0  = 0 + 2 + 2
        //  ArrayList is no solution to the array problem
        nodes.add(offset, objectToInsert);
        return true;
    }

    /**
     * Calculates the base 2 logarithm of an int
     *
     * @param x the int
     * @return log2(x)
     */
    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    /**
     * Calculates the number of nodes at the given depth. Used to calculate the offset for the array list
     * @param depth the depth
     * @return the number of nodes at the given depth
     */
    private int numOfNodesAtDepth(int depth){
        // worldSize / boundingCubeSpacing() = number of indices on this depth
        // multiply by 3 because there are x, y and z indices
        return 3 * worldSize / boundingCubeSpacing(depth);
    }
}
