package org.example.SequenceFinder.Octree;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.example.SequenceFinder.GeometricObjects.Box;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;


/**
 * Describes the view frustum. It is used to identify objects that lay in front or above another object in the octree.
 * <br>
 * <br>
 * A box shaped view frustum is created which looks to the{@link org.example.SequenceFinder.OperatingDirection},
 * any objects that fully or partially lay within an objects view frustum must be removed before the object itself can
 * be removed. <br>
 * <br>
 * The frustum's planes' normals' all point toward the center of the frustum.
 */
public class Frustum {

    /**
     * All six sides of the frustum
     */
    private enum FrustumSides {
        FRONT,
        BOTTOM,
        TOP,
        BACK,
        LEFT,
        RIGHT
    }


    private final Vector3D worldOrigin;
    private final Map<FrustumSides, Plane> planes;

    /**
     * Describes the view frustum. It is used to identify objects that lay in front or above another object in the octree.
     * <br>
     * <br>
     * A box shaped view frustum is created which looks to the{@link org.example.SequenceFinder.OperatingDirection},
     * any objects that fully or partially lay within an objects view frustum must be removed before the object itself can
     * be removed. <br>
     * <br>
     * The frustum's planes' normals' all point toward the center of the frustum.
     *
     * @param front  the side of the box that points towards the operating directions
     * @param bottom bottom plane
     * @param top    top plane
     * @param back   the border of the world at the operating direction
     * @param left   left plane
     * @param right  right plane
     */
    public Frustum(Vector3D worldOrigin, Plane front, Plane bottom, Plane top, Plane back, Plane left, Plane right) {
        this.worldOrigin = worldOrigin;

        // use Hashtable because neither key=null nor value=null are allowed.
        Map<FrustumSides, Plane> temp = new Hashtable<>();
        temp.put(FrustumSides.FRONT, front);
        temp.put(FrustumSides.BOTTOM, bottom);
        temp.put(FrustumSides.TOP, top);
        temp.put(FrustumSides.BACK, back);
        temp.put(FrustumSides.LEFT, left);
        temp.put(FrustumSides.RIGHT, right);

        // the frustum should not be altered after creation
        this.planes = Collections.unmodifiableMap(temp);
    }

    /**
     * Calculates whether the given box lies in or intersects the frustum. <br>
     * <br>
     * Based on <i>Ned Greene</i>'s algorithm in chapter 'Box-Plane and Rectangle-Line Intersection' of his puplication
     * <i>Detecting Intersection of a Rectangular Solid and a Convex Polyhedron</i>, published in
     * <i>Graphics Gems (1994) by Paul S. Heckbert</i>.
     *
     * @param b the given box
     * @return true when any part of the box is visible in this frustum, false otherwise
     */
    public boolean boxInFrustum(Box b) {
        boolean intersects = false;

        // The planes are at such an angle to each other, that if a box is outside one plane, the box is outside all
        // planes, and therefore outside the frustum.
        // A 2D illustration with a trapezoid could be useful for a visualization.
        for (Plane p : planes.values()) {
            // p_<name> represents a Point,
            // v_<name> represents an actual vector.
            //
            // Plane formula:
            // normal = (A, B, C)
            // plane p : normal * (x, y, z) + d = 0        or       p : Ax + By + Cz + d = 0

            // FIXME: more efficient calculation available. The paper says for AABB the n- and p-vertex are always the
            //  same for a given plane and need not be calculated more than once.
            Vector3D p_nVertex = calcNVertex(b);
            Vector3D p_pVertex = calcPVertex(b);
            Vector3D v_normal = p.getNormal().normalize();
            double d = p.getOffset(worldOrigin);

            // FIXME: check if the entire if-chain is correct -> Illustrate it. What if a box is intersecting one plane,
            //  but is behind another?
            if (v_normal.dotProduct(p_pVertex) + d < 0) {
                // b lies entirely in p's negative half-space, and is therefore behind the plane and outside the frustum
                return false;

            } else if (v_normal.dotProduct(p_nVertex) + d > 0) {
                // b lies entirely in p's positive half-space, and is therefore in front of the plane and potentially
                // inside the frustum

            } else {
                // b intersects p
                intersects = true;
            }
        }
        // box is inside all planes and therefore entirely inside the frustum
        return true;
    }

    /**
     * Calculates the positive (aka maximum) vertex of the given box. <br>
     * <br>
     * The p vertex is the corner of the box, that is the furthest along the plane's normal's direction. <br>
     * If the p vertex is behind the plane, the whole box is behind the plane. Because the planes are in a 90-degree
     * angle to their adjazent planes with all their normal's pointing towards the center of the frustum, the box is not
     * just behind a single plane, but outside the frustum. <br>
     * <br>
     * See https://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/ for a
     * visualization
     *
     * @param b the given box
     * @return the p vertex of the box
     */
    private Vector3D calcPVertex(Box b) {
        return null;
    }

    /**
     * Calculates the negative (aka minimum) vertex of the given box. <br>
     * <br>
     * The n vertex is the corner of the box, that is the furthest against the plane's normal's direction. <br>
     * If the p vertex is not behind the plane, the n vertex needs to be tested to decide whether the object is fully
     * or partly within the frustum. <br>
     * <b>Note:</b> currently it doesn't matter whether the box is fully or partly in the frustum. In both cases the
     * box needs to be removed before the box whose side defines the front plane of the frustum can be removed itself.
     * <br>
     * <br>
     * See https://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/ for a
     * visualization
     *
     * @param b the given box
     * @return the n vertex of the box
     */
    private Vector3D calcNVertex(Box b) {
        return null;
    }
}
