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
     * Calculate the cost of the directed edge from nodeA to nodeB in the
     * {@linkplain Graph}
     *
     * @param nodeA a node in the graph
     * @param nodeB a node with an incoming directed edge from nodeA
     * @return the cost / weight of the edge
     */
    double calcCost(T nodeA, T nodeB);
}
