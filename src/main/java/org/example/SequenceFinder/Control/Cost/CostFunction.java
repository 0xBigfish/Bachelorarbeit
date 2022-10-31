package org.example.SequenceFinder.Control.Cost;

import org.example.SequenceFinder.Model.Graph.Graph;

/**
 * Interface for the cost functions, utilized as a strategy pattern. They are used by the
 * {@linkplain org.example.SequenceFinder.Control.CostAssigner}
 *
 * @param <T> the type of objects stored in the {@linkplain Graph}
 */
public interface CostFunction<T> {

    /**
     * Calculate the cost of the directed edge from nodeA to nodeB in the {@linkplain Graph}.
     * <p>
     * <b>The cost must be >= 0 !</b>
     *
     * @param nodeA a node in the graph
     * @param nodeB a node with an incoming directed edge from nodeA
     * @return the cost / weight of the edge
     */
    double calcCost(T nodeA, T nodeB);

    /**
     * The lower bound of the cost function.
     * <p>
     * <b>The lower bound must be >= 0 !</b>
     * <p>
     * In some cases finding a lower bound of a cost function is an optimation problem itself, therefore each cost
     * function must provide a lower bound. The lower bound is needed to calculate the lower bound of a given
     * subsequence in {@linkplain org.example.SequenceFinder.Control.BranchAndBound
     *
     * @return
     */
    double lowerBound();
}
