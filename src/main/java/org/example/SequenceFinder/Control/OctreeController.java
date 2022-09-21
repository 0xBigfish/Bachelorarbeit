package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.Octree.LooseOctree;

import java.util.Collection;

/**
 * Controller for the LooseOctree. It is used to insert a collection of AABB objects into the LooseOctree.
 */
public class OctreeController<T extends AABB> {
    /**
     * The octree where the Boxes will be inserted into
     */
    private final LooseOctree<T> octree;

    /**
     * Controller for the LooseOctree. It is used to insert a collection of AABB objects into the LooseOctree.
     */
    public OctreeController(LooseOctree<T> octree) {
        this.octree = octree;
    }

    /**
     * Insert all AABB objects into the LooseOctree. Returns true when all have been inserted successfully
     *
     * @param c the collection of boxes
     * @return true when all boxes have been inserted, false when one or more boxes could not be inserted
     */
    public boolean insertAll(Collection<T> c) {
        for (T box : c) {
            if (!octree.insertObject(box)) {
                return false;
            }
        }
        return true;
    }
}
