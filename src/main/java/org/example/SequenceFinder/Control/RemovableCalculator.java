package org.example.SequenceFinder.Control;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.Model.Octree.Frustum;
import org.example.SequenceFinder.Model.Octree.LooseOctree;
import org.example.SequenceFinder.OperatingDirection;

import java.util.Collection;

/**
 * Controller to calculate which Boxes are removable in the stack of boxes.
 * <p>
 * It creates a single or multiple Graphs using frustum culling, where nodes with no incoming edges represent the
 * removable Boxes.
 *
 * @see Graph
 */
public class RemovableCalculator<T extends AABB> {

    /**
     * the octree where the boxes are stored
     */
    LooseOctree<T> octree;
    /**
     * the operating directions from where the boxes can be placed or removed
     */
    Collection<OperatingDirection> opDirs;

    /**
     * Controller to calculate which Boxes are removable in the stack of boxes.
     * <p>
     * It creates a single or multiple Graphs using frustum culling, where nodes with no incoming edges represent the
     * removable Boxes.
     *
     * @see Graph
     */
    public RemovableCalculator(LooseOctree<T> octree, Collection<OperatingDirection> operatingDirections) {
        this.octree = octree;
        this.opDirs = operatingDirections;
        if (operatingDirections.isEmpty()) {
            throw new IllegalArgumentException("The RemovableCalculator needs at least one OperatingDirection!");
        }
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
     *     <li>...merging each node with their corresponding node in the other graphs, but remembering which edge came
     *     from which graph. <br>
     *     <b>The resulting graph does allow alternating operating directions</b></li>
     * </ul>
     *
     * @param opDirsCanAlternate whether the operating direction can alternate. This means that is allowed to remove a
     *                           box from one direction, and the next box from another direction, i.e. remove BoxA from
     *                           the left direction and then remove BoxB from the right direction.
     * @return a merged graph as described above
     */
    public Graph<T> createMergedGraph(boolean opDirsCanAlternate) {
        // to be implemented
        return null;
    }

    /**
     * Creates a new  frustum from the aabb in direction of the operating direction.
     * <p>
     * The frustum is used to perform frustum culling onto the octree and calculate which AABB are in front or above the
     * given aabb<br> For example: when the operating direction is 'left', a frustum is created from the left side of
     * the aabb pointing towards the left direction.
     *
     * @param aabb  the aabb for which the frustum will be created
     * @param opDir the operating direction
     * @return a frustum pointing from the aabb towards the operating direction
     */
    private Frustum createFrustum(AABB aabb, OperatingDirection opDir) {
        // the back of the frustum is the side of the aabb that is facing towards the operating direction
        Plane back = aabb.getSide(opDir);

        // the front of the frustum is the border of the world
        // FIXME: normal is pointing in the wrong direction?
        Plane front = octree.getWorldAABB().getSide(opDir.getOpposite());

        Plane left = aabb.getSide(opDir.getLeft());
        Plane right = aabb.getSide(opDir.getRight());
        Plane top = aabb.getSide(opDir.getTop());
        Plane bottom = aabb.getSide(opDir.getBottom());

        return new Frustum(front, back, left, right, top, bottom);
    }


    /**
     * Calculates the collection of Boxes that lie in the frustum. They need to be removed before the AABB, that lies at
     * the very tip of the frustum, can be removed.
     *
     * @param frustum the frustum
     */
    private Collection<T> frustumCulling(Frustum frustum) {
        return octree.cullContent(frustum);
    }
}
