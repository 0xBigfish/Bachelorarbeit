package org.example.SequenceFinder.Control.Cost;

import org.example.SequenceFinder.Model.GeometricObjects.AABB;

public class HeightDifference<T extends AABB> implements CostFunction<T> {

    private final double cost;

    /**
     * Sets the cost per unit of height difference
     *
     * @param costPerUnitDiff the cost per unit of height difference
     */
    public HeightDifference(double costPerUnitDiff) {
        this.cost = costPerUnitDiff;
    }

    /**
     * {@inheritDoc} <br>
     * <br>
     * Returns cost set in the constructor times the height difference of nodeA and nodeB in the world
     *
     * @param nodeA a node in the graph
     * @param nodeB a node with an incoming directed edge from nodeA
     * @return cost set in the constructor times the height difference of nodeA and nodeB in the world
     */
    @Override
    public double calcCost(T nodeA, T nodeB) {
        return cost * Math.abs(nodeA.calcCenter().z - nodeB.calcCenter().z);
    }

    /**
     * {@inheritDoc}
     * @return the minimum of the height difference, which is 0
     */
    @Override
    public double lowerBound() {
        return 0;
    }
}
