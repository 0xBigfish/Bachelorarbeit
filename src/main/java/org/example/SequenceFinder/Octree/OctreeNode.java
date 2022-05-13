package org.example.SequenceFinder.Octree;

import org.example.SequenceFinder.GeometricObjects.Box;

import java.util.Collection;
import java.util.HashSet;

/**
 * A node in the {@linkplain LooseOctree}. <br>
 * <br>
 * Constructor and methods are package protected because they are only used within the {@linkplain LooseOctree}'s
 * package
 *
 * @param <T> a box shaped object
 */
class OctreeNode<T extends Box> {

    private final HashSet<T> content;

    /**
     * Create a new OctreeNode
     */
    OctreeNode() {
        this.content = new HashSet<>();
    }

    /**
     * Insert the given object into the OctreeNode
     *
     * @param toBeInserted the object that will be inserted
     */
    void insertObject(T toBeInserted) {
        this.content.add(toBeInserted);
    }

    /**
     * Insert the given objects into the OctreeNode. Duplicate entries are ignored.
     *
     * @param toBeInserted the objects that will be inserted
     */
    void insertObjects(Collection<T> toBeInserted) {
        this.content.addAll(toBeInserted);
    }


    /**
     * Return the content of this OctreeNode
     *
     * @return the content of this OctreeNode
     */
    HashSet<T> getContent() {
        return content;
    }
}
