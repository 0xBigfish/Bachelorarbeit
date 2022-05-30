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
 * A box shaped view frustum is created which looks to the {@link org.example.SequenceFinder.OperatingDirection},
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
     * Describes the view frustum. It is used to identify objects that lay in front or above another object in the
     * octree. <br>
     * <br>
     * A box shaped view frustum is created which looks to the {@link org.example.SequenceFinder.OperatingDirection},
     * any objects that fully or partially lay within an objects view frustum must be removed before the object
     * itself can be removed. <br>
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
    public Frustum(Vector3D worldOrigin, Plane front, Plane back, Plane left, Plane right, Plane top, Plane bottom) {
        this.worldOrigin = worldOrigin;

        // use Hashtable because neither key=null nor value=null are allowed.
        Map<FrustumSides, Plane> temp = new Hashtable<>();
        temp.put(FrustumSides.FRONT, front);
        temp.put(FrustumSides.BACK, back);
        temp.put(FrustumSides.LEFT, left);
        temp.put(FrustumSides.RIGHT, right);
        temp.put(FrustumSides.TOP, top);
        temp.put(FrustumSides.BOTTOM, bottom);

        // the frustum should not be altered after creation
        this.planes = Collections.unmodifiableMap(temp);
    }

    /**
     * Calculate the {@linkplain Visibility} of the given box with this view frustum <br>
     * <br>
     * Based on <i>Ned Greene</i>'s algorithm in chapter 'Box-Plane and Rectangle-Line Intersection' of his puplication
     * <i>Detecting Intersection of a Rectangular Solid and a Convex Polyhedron</i>, published in
     * <i>Graphics Gems (1994) by Paul S. Heckbert</i>.
     *
     * @param b the given box
     * @return the {@linkplain Visibility} of the given box
     */
    public Visibility calcVisibility(Box b) {
        boolean intersects = false;

        for (Plane p : planes.values()) {
            /*
            Plane formula:
            The normal of each plane points inside the frustum.
            normal = (A, B, C)
            plane p : normal * (x, y, z) + d = 0        or       p : Ax + By + Cz + d = 0

            A point u=(u1, u2, u3) is behind a plane p, iff A*u1 + B*u2 + C*u3 + d < 0.  The other way around,a point
            u=(u1, u2, u3) is in front of a plane p,    iff A*u1 + B*u2 + C*u3 + d > 0. */

            // FIXME: more efficient calculation available. The paper says for AABB the n- and p-vertex are always the
            //  same for a given plane and need not be calculated more than once.
            Vector3D pVertex = calcPVertex(b); // is a point
            Vector3D nVertex = calcNVertex(b); // is a point
            Vector3D normalVector = p.getNormal().normalize(); // is a vector
            double d = p.getOffset(worldOrigin);

            /*
            Box is in front of all planes               => inside the frustum
            Box is behind one or more planes            => outside the frustum
            Box is intersecting at least one plane AND
            is not behind any plane                     => intersecting the frustum

            A 2D illustration with a trapezoid could be useful for a visualization. */
            if (normalVector.dotProduct(pVertex) + d < 0) {
                // b lies entirely in p's negative half-space, and is therefore behind the plane and outside the frustum
                return Visibility.NOT_VISIBLE;

            } else if (normalVector.dotProduct(nVertex) + d > 0) {
                // b lies entirely in p's positive half-space, and is therefore in front of the plane and potentially
                // inside the frustum
                continue;

            } else {
                // b intersects p
                intersects = true;
            }
        }

        if (intersects) {
            return Visibility.PARTLY_VISIBLE;
        } else {
            return Visibility.FULLY_VISIBLE;
        }
    }

    /**
     * Calculates the positive (aka maximum) vertex of the given box. <br>
     * <br>
     * The p vertex is the corner of the box, that is the furthest along the plane's normal's direction. <br>
     * If the p vertex is behind the plane, the whole box is behind the plane. <br>
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
