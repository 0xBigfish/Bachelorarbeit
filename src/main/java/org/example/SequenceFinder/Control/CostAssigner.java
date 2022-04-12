package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Control.Cost.CostFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Controller to assign cost based on the list of given{@link org.example.SequenceFinder.Control.Cost.CostFunction}s
 * `
 *
 * @param <T> the type of node in the {@linkplain org.example.SequenceFinder.Model.Graph}
 */
public class CostAssigner<T> {
    ArrayList<CostFunction<T>> costFunctions;

    /**
     * Controller to assign cost based on the list of given{@link org.example.SequenceFinder.Control.Cost.CostFunction}s
     * `
     *
     * @param costFunctions the cost functions that will be evaluated to calculate the total costs
     */
    public CostAssigner(Collection<CostFunction<T>> costFunctions) {
        this.costFunctions = new ArrayList<>(costFunctions);
    }

    /**
     * Evaluate all cost functions and sum up their results
     *
     * @param nodeA a node in the graph
     * @param nodeB a node in the graph with an incoming directed edge from nodeA
     * @return the sum of all CostFunctions
     */
    public double calcCost(T nodeA, T nodeB) {
        return costFunctions.stream()
                .mapToDouble(costFunc -> costFunc.calcCost(nodeA, nodeB))
                .sum();
    }
}
