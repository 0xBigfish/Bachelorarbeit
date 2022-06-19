package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Control.Cost.CostFunction;
import org.example.SequenceFinder.Model.GeometricObjects.Box;
import org.example.SequenceFinder.Model.Graph;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Controller to find the optimum sequence to build the stack of boxes, using a Branch And Bound algorithm. <br>
 * <br>
 * First, the tree of all possible sequences is created based on the given {@link Graph}. The BnB assigns cost
 * on-the-fly, based on the cost function, to each traversed edge in the tree. The path with the lowest cost is the
 * optimum.<br>
 * Second, the tree is traversed down to a single leaf using depth-first. Hereby the BnB gets a reference value to
 * compare against. <br>
 * Third, the tree is traversed using breadth-first. Each path which costs get equal or higher than the reference cost
 * are pruned, because this path can only get worse in terms of cost. <br>
 * Fourth, the paths, that were traversed down to a leaf, are compared based on their cost. The path with the lowest
 * cost is the global optimum.
 *
 * @param <T> the type of objects that are stored in the graph
 */
public class BranchAndBound<T extends Box> {

    Graph<T> graph;
    CostAssigner<T> costAssigner;

    /**
     * Branch And Bound algorithm to find the global optimum sequence. <br>
     * <br>
     * First, the tree of all possible sequences is created based on the given {@link Graph}. The BnB assigns cost
     * on-the-fly, based on the cost function, to each traversed edge in the tree. The path with the lowest cost is the
     * optimum.<br>
     * Second, the tree is traversed down to a single leaf using depth-first. Hereby the BnB gets a reference value to
     * compare against. <br>
     * Third, the tree is traversed using breadth-first. Each path which costs get equal or higher than the reference cost
     * are pruned, because this path can only get worse in terms of cost. <br>
     * Fourth, the paths, that were traversed down to a leaf, are compared based on their cost. The path with the lowest
     * cost is the global optimum.
     *
     * @param graph the graph defines the order in which the boxes can be removed
     */
    public BranchAndBound(Graph<T> graph, Collection<CostFunction<T>> costFunctions) {
        this.graph = graph;
        this.costAssigner = new CostAssigner<>(costFunctions);
    }

    /**
     * Calculate the global optimum sequence to build the stack of boxes.
     *
     * @return the optimum sequence to build the stack of boxes
     */
    public LinkedHashSet<T> findGlobalOptimumSequence() {
        // to be implemented
        // return a LinkedHashSet because no duplicate entries are allowed and the sequence fixed
        return null;
    }
}
