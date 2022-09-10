package org.example.SequenceFinder.Model.Octree;

import org.example.SequenceFinder.Model.GeometricObjects.AABB;

import java.util.Collection;
import java.util.HashSet;

/**
 * A node in the {@linkplain LooseOctree}. <br>
 * <br>
 * Constructor and methods are package protected because they are only used within the {@linkplain LooseOctree}'s
 * package
 *
 * @param <T> the type of objects that will be stored in the node
 */
class OctreeNode<T> {

    private final HashSet<T> content;
    private final AABB boundingBox;
    private final HashSet<OctreeNode<T>> children;

    /**
     * Create a new OctreeNode
     */
    OctreeNode(AABB boundingBox) {
        this.content = new HashSet<>();
        this.boundingBox = boundingBox;
        this.children = new HashSet<>();
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
     * Add the given child to this node
     *
     * @param child the child
     */
    private void addChild(OctreeNode<T> child) {
        this.children.add(child);
    }

    /**
     * Set the parent of this node.
     *
     * @param parent the parent
     */
    void setParent(OctreeNode<T> parent) {
        parent.addChild(this);
    }

    /**
     * Return the content of this OctreeNode
     *
     * @return the content of this OctreeNode
     */
    HashSet<T> getContent() {
        return content;
    }

    AABB getAABB() {
        return boundingBox;
    }
}
