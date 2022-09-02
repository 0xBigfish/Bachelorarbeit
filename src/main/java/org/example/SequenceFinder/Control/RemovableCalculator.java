package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Model.GeometricObjects.Box;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.Model.Octree.Frustum;
import org.example.SequenceFinder.Model.Octree.LooseOctree;
import org.example.SequenceFinder.Model.Octree.Visibility;
import org.example.SequenceFinder.OperatingDirection;

import java.util.Collection;

/**
 * Controller to calculate which Boxes are removable in the stack of boxes. <br>
 * It creates a single or multiple Graphs using frustum culling, where nodes with no incoming edges represent the
 * removable Boxes.
 *
 * @see Graph
 */
public class RemovableCalculator<T extends Box> {

    /**
     * the octree where the boxes are stored
     */
    LooseOctree<T> octree;
    /**
     * the operating directions from where the boxes can be placed or removed
     */
    Collection<OperatingDirection> opDirs;

    /**
     * Controller to calculate which Boxes are removable in the stack of boxes. <br>
     * It creates a single or multiple Graphs using frustum culling, where nodes with no incoming edges represent the
     * removable Boxes.
     *
     * @see Graph
     */
    public RemovableCalculator(LooseOctree<T> octree, Collection<OperatingDirection> operatingDirections) {
        this.octree = octree;
        this.opDirs = operatingDirections;
    }

    /**
     * Creates a graph using frustum culling, where all nodes with no incoming edges represent removable Boxes in the
     * stack of boxes
     *
     * @param opDir the direction from where the Boxes can be removed.
     * @return the graph as described above
     */
    public Graph<T> createGraph(OperatingDirection opDir) {
        // to be implemented
        return null;
    }

    /**
     * Creates a merged graph of all possible operating directions. The graph is merged either by...
     * <ul>
     *     <li>...introducing a 'master root' node and 'direction root' nodes. The master root node has directed edges
     *     with no cost to every direction root node. Each direction root node has a directed edge with no cost to each
     *     node that can be removed, therefore all nodes that have no incoming edges.<br>
     *     <b>The resulting graph does not allow alternating operating directions</b></li>
     *
     *     <li>...merging each node which their corresponding node in the other graphs, but remembering which edge came
     *     from which graph. <br>
     *     <b>The resulting graph does allow alternating operating directions</b></li>
     * </ul>
     *
     * @param opDirsCanAlternate whether the operating direction can alternate. This means that is allowed to remove a
     *                           Box from one direction, and the next Box from another direction, i.e. remove BoxA from
     *                           the left direction and then remove BoxB from the right direction.
     * @return a merged graph as described above
     */
    public Graph<T> createMergedGraph(boolean opDirsCanAlternate) {
        // to be implemented
        return null;
    }

    /**
     * Creates a new  frustum from the box in direction of the operating direction. The frustum is used to perform
     * frustum culling onto the octree and calculate which Box are in front or above the given box<br>
     * For example: when the oparting direction is 'left', a frustum is created from the left side of the box pointing
     * towards the left direction.
     *
     * @param box   the box for which the frustum will be created
     * @param opDir the operating direction
     * @return a frustum pointing from the box towards the operating direction
     */
    private Frustum createFrustum(Box box, OperatingDirection opDir) {
        // to be implemented
        return null;
    }

    //TODO: check if the return value is correct

    /**
     * Calculates the collection of Boxes that lie in the frustum, and therefore need to be removed, before the Box,
     * that lies at the very tip of the frustum, can be removed.
     *
     * @param f the frustum
     * @param v the visibility
     */
    private Collection<Box> cullFrustum(Frustum f, Visibility v) {
        // to be implemented
        return null;
    }
}
