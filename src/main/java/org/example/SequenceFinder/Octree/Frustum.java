package org.example.SequenceFinder.Octree;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.example.SequenceFinder.GeometricObjects.Box;
import org.example.SequenceFinder.GeometricObjects.Point;

import java.util.ArrayList;
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
        BACK,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }


    private final Vector3D worldOrigin;
    private final Map<FrustumSides, Plane> planesMap;

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
    public Frustum(Plane front, Plane back, Plane left, Plane right, Plane top, Plane bottom) {
        // the LooseOctree assumes the world is centered at the coordinate system's origin, which is (0, 0, 0).
        this.worldOrigin = new Vector3D(0, 0, 0);

        // use Hashtable because neither key=null nor value=null are allowed.
        Map<FrustumSides, Plane> temp = new Hashtable<>();
        temp.put(FrustumSides.FRONT, front);
        temp.put(FrustumSides.BACK, back);
        temp.put(FrustumSides.LEFT, left);
        temp.put(FrustumSides.RIGHT, right);
        temp.put(FrustumSides.TOP, top);
        temp.put(FrustumSides.BOTTOM, bottom);

        // the frustum should not be altered after creation
        this.planesMap = Collections.unmodifiableMap(temp);

        // FIXME: checkout
        //  https://math.stackexchange.com/questions/3114932/determine-direction-of-normal-vector-of-convex-polyhedron-in-3d
        if (!allNormalsPointInside()) {
            throw new IllegalArgumentException("All plane normals must point inside the frustum!");
        }
    }

    /**
     * Checks if all plane normals point inside the frustum. This is a necessary condition for this frustum
     * implementation
     *
     * @return true when all plane normals point inside the frustum, false otherwise
     */
    private boolean allNormalsPointInside() {
        // FIXME: just an idea, doesn't work when frustum is looing in any other direction than x
        // front and back
        Vector3D pointOnFront = planesMap.get(FrustumSides.FRONT).getOrigin();
        Vector3D pointOnBack = planesMap.get(FrustumSides.BOTTOM).getOrigin();

        // left and right
        Vector3D pointOnLeft = planesMap.get(FrustumSides.LEFT).getOrigin();
        Vector3D pointOnRight = planesMap.get(FrustumSides.RIGHT).getOrigin();

        // top and bottom
        Vector3D pointOnTop = planesMap.get(FrustumSides.TOP).getOrigin();
        Vector3D pointOnBottom = planesMap.get(FrustumSides.BOTTOM).getOrigin();

        // the middle of the frustum (does not need to be 100% accurate)
        Vector3D pointMiddleOfFrustum = new Vector3D(
                (pointOnFront.getX() + pointOnBack.getX()) / 2,
                (pointOnLeft.getY() + pointOnRight.getY()) / 2,
                (pointOnBottom.getZ() + pointOnTop.getZ()) / 2
        );

        // pointMiddleOfFrustum should be in front of all planes, otherwise there is at least one plane normal that
        // is not pointing inside the frustum
        for (Plane plane : planesMap.values()) {
            if (plane.getNormal().dotProduct(pointMiddleOfFrustum) + plane.getOffset(worldOrigin) < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean allNormalsPointInwards() {
        // FIXME: just an idea
        for (int i = 0; i < 6; i++) {
            // the opposite plane is one index ahead: 0=front, 1=back, 2=left, 3=right; ...
            Plane firstPlane = planesMap.get(FrustumSides.values()[i]);
            Plane oppositePlane = planesMap.get(FrustumSides.values()[i + 1]);

            Vector3D normalFirstPlane = firstPlane.getNormal();
            Vector3D normalOppositePlane = oppositePlane.getNormal();
            double dotProduct = normalFirstPlane.dotProduct(normalOppositePlane);

            // planes are parallel
            if (dotProduct == 0) {
                Vector3D originFirstPlane = firstPlane.getOrigin();
                Vector3D originOppositePlane = oppositePlane.getOrigin();
                double dFirstPlane = firstPlane.getOffset(worldOrigin);
                double dOppositePlane = oppositePlane.getOffset(worldOrigin);

                if (normalFirstPlane.dotProduct(originOppositePlane) + dFirstPlane < 0 ||
                        normalOppositePlane.dotProduct(originFirstPlane) + dOppositePlane < 0) {
                    // one or both planes are behind the other => one or both normals point outside the frustum
                    return false;
                }
            }
            i++;
        }
        // if parallel:
        //      check if origin of each plane is in front of the other plane
        // if not parallel:
        //      c
        return true;
    }

    private Vector3D centerBetweenPlanes(Plane planeA, Plane planeB) {
        Vector3D originA = planeA.getOrigin();
        Vector3D originB = planeB.getOrigin();

        return new Vector3D(
                (originA.getX() + originB.getX()) / 2,
                (originA.getY() + originB.getY()) / 2,
                (originA.getZ() + originB.getZ()) / 2
        );
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

        for (Plane p : planesMap.values()) {
            /*
            Plane formula:
            The normal of each plane points inside the frustum.
            normal = (A, B, C)
            plane p : normal * (x, y, z) + d = 0        or       p : Ax + By + Cz + d = 0

            A point u=(u1, u2, u3) is behind a plane p, iff A*u1 + B*u2 + C*u3 + d < 0.  The other way around,a point
            u=(u1, u2, u3) is in front of a plane p,    iff A*u1 + B*u2 + C*u3 + d > 0. */

            // FIXME: more efficient calculation available. The paper says for AABB the n- and p-vertex are always the
            //  same for a given plane and need not be calculated more than once.
            Vector3D pVertex = calcPVertex(b, p); // is a point
            Vector3D nVertex = calcNVertex(b, p); // is a point
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
     * @param box   the given box
     * @param plane the plane
     * @return the p vertex of the box
     */
    private Vector3D calcPVertex(Box box, Plane plane) {
        ArrayList<Point> vertices = box.getVertices();
        Vector3D normal = plane.getNormal();
        double d = plane.getOffset(worldOrigin);

        // get a start value to compare against
        Point pVertex = vertices.remove(0);
        double distanceAlongNormal = normal.dotProduct(new Vector3D(pVertex.x, pVertex.y, pVertex.z)) + d;

        for (Point vertex : vertices) {
            // if the current vertex is further ALONG the plane's normal than the current pVertex, the pVertex
            // is updated
            if (normal.dotProduct(new Vector3D(vertex.x, vertex.y, vertex.z)) + d > distanceAlongNormal) {
                pVertex = vertex;
            }
        }

        return new Vector3D(pVertex.x, pVertex.y, pVertex.z);
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
     * @param box   the given box
     * @param plane the plane
     * @return the n vertex of the box
     */
    private Vector3D calcNVertex(Box box, Plane plane) {
        ArrayList<Point> vertices = box.getVertices();
        Vector3D normal = plane.getNormal();
        double d = plane.getOffset(worldOrigin);

        // get a start value to compare against
        Point nVertex = vertices.remove(0);
        double distanceAlongNormal = normal.dotProduct(new Vector3D(nVertex.x, nVertex.y, nVertex.z)) + d;

        for (Point vertex : vertices) {
            // if the current vertex is further AGAINST the plane's normal than the current nVertex, the nVertex
            // is updated
            if (normal.dotProduct(new Vector3D(vertex.x, vertex.y, vertex.z)) + d < distanceAlongNormal) {
                nVertex = vertex;
            }
        }

        return new Vector3D(nVertex.x, nVertex.y, nVertex.z);
    }
}
