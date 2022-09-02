package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Control.Cost.CostFunction;
import org.example.SequenceFinder.Model.GeometricObjects.Box;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.Model.Octree.LooseOctree;
import org.example.SequenceFinder.OperatingDirection;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Main Controller of the SequenceFinder package. <br>
 * <br>
 * It utilizes the underlying controllers to calculate the global optimum sequence for the given stack of boxes. <br>
 * First, the boxes are inserted into a spatial data structure, namely a loose octree. Then a graph is generated,
 * which represents the dependencies between the boxes before each can be removed. The graph
 * implies the tree of all possible sequences to build the stack. This tree of all possible sequences, or respectively
 * the graph, is then traversed by a Branch And Bound algorithm to find the global optimum sequence, based on a number
 * of cost functions. <br>
 * The sequence with the lowest total cost over all cost functions is the global optimum.
 *
 * @see CostFunction
 */
public class SequenceFinder<T extends Box> {

    /**
     * The max depth of the loose octree
     */
    int maxOctreeDepth;
    /**
     * Determines the size of the loose octree. Must include all boxes, otherwise they are not part of the calculation
     */
    int worldSize;
    /**
     * All available operating directions, respectively the positions from where the stack of boxes can be build
     */
    Collection<OperatingDirection> operatingDirections;

    /**
     * Main Controller of the SequenceFinder package. <br>
     * <br>
     * It utilizes the underlying controllers to calculate the global optimum sequence for the given stack of boxes.<br>
     * First, the boxes are inserted into a spatial data structure, namely a loose octree. Then a graph is generated,
     * which represents the dependencies between the boxes before each can be removed. The graph
     * implies the tree of all possible sequences to build the stack. This tree of all possible sequences, or
     * respectively the graph, is then traversed by a Branch And Bound algorithm to find the global optimum sequence,
     * based on a number of cost functions. <br>
     * The sequence with the lowest total cost over all cost functions is the global optimum.
     *
     * @param maxOctreeDepth      the max depth of the loose octree
     * @param worldSize           the size of the world in each direction from the world's center. The world MUST be
     *                            large enough to include all boxes, it COULD therefore be centered at the center of
     *                            the stack of boxes or the center of the space they are placed in or on.
     * @param operatingDirections a list of all available operating directions from where to build the stack of boxes
     * @see CostFunction
     */
    public SequenceFinder(int maxOctreeDepth, int worldSize, List<OperatingDirection> operatingDirections) {
        this.maxOctreeDepth = maxOctreeDepth;
        this.worldSize = worldSize;
        this.operatingDirections = operatingDirections;
    }

    /**
     * Calculates the optimum sequence for the collection of boxes based on the defined cost functions. The optimum
     * sequence is the one with the lowest total cost over all cost functions.
     *
     * @param boxes              a collection of boxes
     * @param opDirsCanAlternate whether the operating directions can alternate between box placements
     * @param costFunctions      the cost functions which will decide the optimum sequence
     * @return the optimum sequence
     */
    public LinkedHashSet<T> calcOptSequence(Collection<T> boxes, boolean opDirsCanAlternate,
                                            Collection<CostFunction<T>> costFunctions) {
        // insert the boxes into the octree
        LooseOctree<T> looseOctree = new LooseOctree<>(maxOctreeDepth, worldSize);
        OctreeController<T> octreeInserter = new OctreeController<>(looseOctree);
        if (!octreeInserter.insertAll(boxes)) {
            throw new IllegalArgumentException(
                    "Something went wrong! Not all boxes could be inserted into the loose Octree!"
            );
        }

        // generate the graph which implies all possible sequences
        RemovableCalculator<T> removableCalculator = new RemovableCalculator<>(looseOctree, operatingDirections);
        Graph<T> graph = removableCalculator.createMergedGraph(opDirsCanAlternate);

        // use the branch and bound algorithm to find the global optimum sequence, by minimizing the total cost, which
        // are the sum of all cost functions
        BranchAndBound<T> branchNBound = new BranchAndBound<>(graph, costFunctions);

        // find the global optimum sequence
        return branchNBound.findGlobalOptimumSequence();
    }
}
