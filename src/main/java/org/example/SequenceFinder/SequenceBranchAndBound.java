package org.example.SequenceFinder;

import org.example.SequenceFinder.GeometricObjects.Box;

import java.util.Map;

/**
 * Find the best sequence by using a branch and bound algorithm on the generated graphs. The cheapest path is the best.
 */
public class SequenceBranchAndBound {

    /**
     * each operating direction has its own graph. To find the best solution all possible graphs need to be compared
     */
    private Map<OperatingDirection, Graph<Box>> graphs;

}
