package org.example.SequenceFinder.Octree;

import org.example.SequenceFinder.GeometricObjects.Box;
import org.example.SequenceFinder.GeometricObjects.Point;

/**
 * A Loose Octree to store each object based on its position in the world. Used to improve performance for frustum culling
 * <br>
 * <br>
 * The tree implementation is based on Thatcher Ulrich's article about Loose Octrees in "Game Programming Gems" (2000)
 * (ISBN 1-58450-049-2). <br>
 * A first introduction can be found on his website: http://www.tulrich.com/geekstuff/partitioning.html
 *
 * @param <T> a box shaped object.
 */
public class LooseOctree<T extends Box> {

    /**
     * the maximum depth of the octree
     */
    private final int maxDepth;

    /**
     * array holding all objects in the octree. <br>
     * The first dimension represents depth, the second, third dimension and fourth
     * represent the x,y,z index in the tree. <br>
     * <br>
     * There should be no item(s) at depth 0, as items are inserted based on their radius and position. Nodes are only
     * places at depth 0 if their radius is larger than worldSize / 2, but then the object would be bigger than the
     * entire world, which is a paradox.
     */
    // protected to enable test class access to this field<
    protected Box[][][][] nodes;

    /**
     * size of the world. Large enough to fit every object into it. Should be a multiple of 2
     */
    private final int worldSize;

    /**
     * factor used to make the Octree loose. A value of > 1 loosens the tree. <br>
     * <br>
     * According to Thatcher Ulrich in "Game Programming Gems (2000), Loose Octrees" a value of k=2 is a good balance
     * between loose but not too loose. <br>
     * <br>
     * The factor of 2 is also necessary for the mathematical derivation of the insertion formula, which
     * enables insertion in O(1)
     */
    private final double k = 2;

    public LooseOctree(int maxDepth, int worldSize) {
        this.maxDepth = maxDepth;
        //TODO: decide if useful to force worldSize to be 2^x
        this.worldSize = worldSize;

        // init array to be able to overwrite the values in the for loop
        // use maxDepth + 1 because only the imaginary root node is at level 0 and maxDepth describes the number of
        // edges to the deepest node(s)
        nodes = new Box[maxDepth + 1][0][0][0];

        // init the array correctly to hold only the maximum allowed number of nodes per depth:
        //  depth=1 => 2 nodes per Dimension and 8 total, depth=2 => 4 nodes per dimension and 16 total, ...
        for (int i = 1; i <= maxDepth; i++) {
            // worldSize / boundingCubeSpacing() = number of indices at this depth
            // multiply by 3 because there are x, y and z indices
            int numOfNodesPerDim = (int) Math.pow(2, i);
            nodes[i] = new Box[numOfNodesPerDim][numOfNodesPerDim][numOfNodesPerDim];
        }
    }

    /**
     * Length of the loosened bounding cubes which recursively divide the world in 8 cubes
     *
     * @param depth the depth
     * @return the length of the bounding cubes at the given depth
     */
    public double boundingCubeLength(int depth) {
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
        if (depth <= 0) {
            throw new IllegalArgumentException("Depth was: " + depth + "\n" +
                    "Depth must be larger than 0. There only is the imaginary root node at" +
                    "depth 0, which has no spacing to other nodes, as there are none. \n" +
                    "Depths below 0 make no sense as the highest node (the imaginary root node) lies at depth 0.");
        }

        if (depth > maxDepth){
            throw new IllegalArgumentException("Depth exceeded maxDepth. \n" +
                    "depth: "+ depth + ", maxDepth: " + maxDepth);
        }

            return worldSize / (int) Math.pow(2, depth);
    }

    /**
     * Calculates at which depth in the object will fit in the tree, based on the object's radius <br>
     * <br>
     * A given level in the octree can accommodate any abject whose radius is less than or equal to 1/4 of the bounding
     * cube edge length, regardless of its position. Any object with a radius <= 1/8 of the bounding cube edge length
     * should go in the next deeper level in the tree.
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
     * @return the indices wrapped in a Point object. The Point is just a wrapper to store the values
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
     * Inserts an object into the octree. <br>
     * The insertion assumes the world is centered at the coordinate system origin. <br>
     * <br>
     * Note: this procedure is not ideal, as it does not find the tightest possible containing node for all cases. To
     * find the tightest possible containing node, the child nodes of the calculated node must be checked whether the
     * object fits into one of them or not.
     *
     * @param objectToInsert the object that will be inserted
     * @return returns true if inserted successfully
     */
    public boolean insertObject(T objectToInsert) {
        return false;
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
     *
     * @param depth the depth
     * @return the number of nodes at the given depth
     */
    private int numOfNodesAtDepth(int depth) {
        // worldSize / boundingCubeSpacing() = number of indices on this depth
        // multiply by 3 because there are x, y and z indices
        return 3 * worldSize / boundingCubeSpacing(depth);
    }
}
