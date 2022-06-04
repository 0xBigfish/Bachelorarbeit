package org.example.SequenceFinder.Octree;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.example.SequenceFinder.GeometricObjects.Box;
import org.example.SequenceFinder.GeometricObjects.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.example.SequenceFinder.Octree.Visibility.*;

import static org.junit.jupiter.api.Assertions.*;

class FrustumTest {

    /**
     * Concrete implementation of the abstract {@linkplain Box} class
     */
    private static class ConcreteBox extends Box {
        public ConcreteBox(Point vertA, Point vertB) {
            super(vertA, vertB);
        }
    }


    /**
     * tolerance below which points are considered identical
     */
    static double TOLERANCE = 10E-09;
    Frustum frustum;
    Box box;


    @Nested
    @DisplayName("given a Frustum ")
    class GivenFrustum {

        @BeforeEach
        void setup() {
            Vector3D worldOrigin = new Vector3D(0, 0, 0);

            // new Plane(Point, normalVector, tolerance)
            Plane top = new Plane(new Vector3D(0, 0, 2), new Vector3D(0, 0, -1), TOLERANCE);
            Plane bottom = new Plane(new Vector3D(0, 0, -1), new Vector3D(0, 0, 1), TOLERANCE);

            // 45° angle
            Plane left = new Plane(new Vector3D(2, 0, 0), new Vector3D(1, -1, 0), TOLERANCE);
            // 45° angle
            Plane right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 1, 0), TOLERANCE);

            Plane front = new Plane(new Vector3D(10, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
            Plane back = new Plane(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);

            frustum = new Frustum(worldOrigin, front, back, left, right, top, bottom);
        }

        @Nested
        @DisplayName("when a box is completely inside the frustum")
        class BoxInsideFrustum {

            @BeforeEach
            void setup() {
                box = new ConcreteBox(new Point(2, -1, -0.5), new Point(3, 1, 0.5));
            }

            @Test
            @DisplayName("then the box should be fully visible")
            void fullyVisible() {
                assertEquals(FULLY_VISIBLE, frustum.calcVisibility(box));
            }
        }


        @Nested
        @DisplayName("when a box is outside the frustum")
        class BoxOutsideFrustum {

        }


        @Nested
        @DisplayName("when a box intersects the frustum")
        class BoxIntersectsFrustum {

        }


        @Nested
        @DisplayName("when a box intersects a plane, but is behind another plane")
        class IntersectsOneBehindAnother {

        }
    }


    @Nested
    @DisplayName("given a box shaped Frustum")
    class GivenBoxFrustum {

    }


    @Nested
    @DisplayName("given a Frustum with switched left and right planes ")
    class GivenFrustumLeftRightSwitched {

    }
    // TODO: more tests for top bottom switch, front back switch, front left switch, ... ?


    @Nested
    @DisplayName("given a Frustum which planes' normals point outwards instead of inwards")
    class GivenFrustumNormalOutwards {

    }

    // TODO: look up edge case of VFCart paper and decide if it can happen in this implementation


}