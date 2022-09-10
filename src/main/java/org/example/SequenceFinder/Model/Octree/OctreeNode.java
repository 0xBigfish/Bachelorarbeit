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
class OctreeNode<T extends AABB> {

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

    /**
     * Cull the frustum against this node and its subtree, and return the objects that fully or partly lay within the
     * frustum.
     * <p>
     * The initial call to this method should be made with an empty {@linkplain Collection} of visible objects.
     *
     * @param frustum        the frustum that will be used to cull the Octree
     * @param visibleObjects the objects that are known to be visible. Should be initially empty.
     * @return the objects of this node and its subtree that lay within the frustum
     */
    Collection<T> cullFrustum(Frustum frustum, Collection<T> visibleObjects) {
        Visibility nodeVisibility = frustum.calcVisibility(boundingBox);

        if (nodeVisibility == Visibility.NOT_VISIBLE) {
            // return the visible objects without adding anything
            return visibleObjects;

        } else if (nodeVisibility == Visibility.FULLY_VISIBLE) {
            // the node and all its child nodes are fully visible, therefore the content of the whole subtree is visible
            visibleObjects.addAll(getSubtreeContent());
            return visibleObjects;

        } else if (nodeVisibility == Visibility.PARTLY_VISIBLE) {
            // check the visibility of this node's content
            for (T object : content) {
                if (frustum.calcVisibility(object) != Visibility.NOT_VISIBLE) {
                    visibleObjects.add(object);
                }
            }

            // check each child node for their visibility
            for (OctreeNode<T> child : children) {
                visibleObjects.addAll(child.cullFrustum(frustum, visibleObjects));
            }
            return visibleObjects;

        } else {
            throw new IllegalStateException("Visibility is not defined");
        }
    }

    /**
     * Get the content of the whole subtree of this node (including the content of this node)
     *
     * @return the content of the whole subtree of this node (including the content of this node)
     */
    Collection<T> getSubtreeContent() {
        Collection<T> subtreeContent = new HashSet<>(content);

        if (children.isEmpty()) {
            return subtreeContent;
        } else {
            for (OctreeNode<T> child : children) {
                subtreeContent.addAll(child.getSubtreeContent());
            }
        }
        return subtreeContent;
    }
}
