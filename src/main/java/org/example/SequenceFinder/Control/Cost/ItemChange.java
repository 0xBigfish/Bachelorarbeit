package org.example.SequenceFinder.Control.Cost;

import org.example.SequenceFinder.Model.GeometricObjects.Package;

/**
 * Sets the cost for when an item change occurs, namely that the articleNo of the two packages differs. <br>
 * <br>
 * Implements {@link CostFunction} interface
 */
public class ItemChange implements CostFunction<Package> {
    private final double cost;

    /**
     * Sets the cost for when an item change occurs, namely that the articleNo of the two packages differs. <br>
     * <br>
     * Implements {@link CostFunction} interface
     *
     * @param cost the cost for the item change
     */
    public ItemChange(double cost) {
        this.cost = cost;
    }

    /**
     * {@inheritDoc} <br>
     * <br>
     * Return the cost specified in the constructor if the articleNo of nodeA and nodeB differ, return 0 otherwise.
     *
     * @param nodeA a node in the graph
     * @param nodeB a node with an incoming directed edge from nodeA
     * @return the cost specified in the constructor if the articleNo of nodeA and nodeB differ, return 0 otherwise
     */
    @Override
    public double calcCost(Package nodeA, Package nodeB) {
        if (nodeA.getArticleNo() != nodeB.getArticleNo()) {
            return cost;

        } else {
            return 0;
        }
    }
}
