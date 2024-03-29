package org.example.SequenceFinder.Model.Octree;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.GeometricObjects.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.example.SequenceFinder.Model.Octree.Visibility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FrustumTest {

    /**
     * tolerance below which points are considered identical
     */
    static double TOLERANCE = 10E-09;
    Frustum frustum;
    AABB box;


    /**
     * Concrete implementation of the abstract {@linkplain AABB} class
     */
    private static class ConcreteAABB extends AABB {
        public ConcreteAABB(Point vertA, Point vertB) {
            super(vertA, vertB);
        }
    }


    @Nested
    @DisplayName("Frusta looking in X direction")
    class XDirection {

        @Nested
        @DisplayName("given a 90 degree Frustum")
        class GivenFrustum {

            @BeforeEach
            void setup() {

                // new Plane(Point, normalVector, tolerance)
                Plane top = new Plane(new Vector3D(0, 0, 2), new Vector3D(0, 0, -1), TOLERANCE);
                Plane bottom = new Plane(new Vector3D(0, 0, -1), new Vector3D(0, 0, 1), TOLERANCE);

                // 45° angle
                Plane left = new Plane(new Vector3D(0, 2, 0), new Vector3D(1, -1, 0), TOLERANCE);
                // 45° angle
                Plane right = new Plane(new Vector3D(0, -2, 0), new Vector3D(1, 1, 0), TOLERANCE);

                Plane front = new Plane(new Vector3D(10, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                Plane back = new Plane(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);

                frustum = new Frustum(front, back, left, right, top, bottom);
            }

            @Nested
            @DisplayName("when a box is completely inside the frustum")
            class AABBInsideFrustum {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(2, -1, -0.5), new Point(3, 1, 0.5));
                }

                @Test
                @DisplayName("then the box should be fully visible")
                void fullyVisible() {
                    assertEquals(FULLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box is outside the frustum")
            class AABBOutsideFrustum {

                @BeforeEach
                void setup() {
                    // behind every plane except the top plane
                    box = new ConcreteAABB(new Point(-1, 2.5, -1.5), new Point(-0.5, 3, -1.25));
                }

                @Test
                @DisplayName("then the box should not be visible")
                void notVisible() {
                    assertEquals(NOT_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects the frustum")
            class AABBIntersectsFrustum {

                @BeforeEach
                void setup() {
                    // intersects only the right plane
                    box = new ConcreteAABB(new Point(-1, -4, 0), new Point(1, -0.5, 1.5));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box's side lays in a frustum plane but intersects no other planes")
            class AABBSideLaysInAPlane {

                @BeforeEach
                void setup() {
                    // half the box's vertices lay on the back plane but intersect no other planes
                    box = new ConcreteAABB(new Point(0, -1, 0), new Point(1, 1, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }

            }


            @Nested
            @DisplayName("when a box intersects two frustum planes")
            class AABBIntersectsTwoPlanes {

                @BeforeEach
                void setup() {
                    // intersects the left plane and the back plane in the back-left corner
                    box = new ConcreteAABB(new Point(8, 8, 0), new Point(12, 12, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when the frustum is completely inside the box")
            class FrustumInAABB {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-5, -15, -2), new Point(15, 5, 3));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void notVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }
        }


        @Nested
        @DisplayName("given a rectangular shaped Frustum")
        class GivenRectangularFrustum {

            @BeforeEach
            void setup() {

                // new Plane(Point, normalVector, tolerance)
                Plane top = new Plane(new Vector3D(0, 0, 3), new Vector3D(0, 0, -1), TOLERANCE);
                Plane bottom = new Plane(new Vector3D(0, 0, 0.5), new Vector3D(0, 0, 1), TOLERANCE);

                // left and right plane are parallel
                Plane left = new Plane(new Vector3D(0, 2, 0), new Vector3D(0, -1, 0), TOLERANCE);
                Plane right = new Plane(new Vector3D(0, -2, 0), new Vector3D(0, 1, 0), TOLERANCE);

                Plane front = new Plane(new Vector3D(4, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                Plane back = new Plane(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);

                frustum = new Frustum(front, back, left, right, top, bottom);
            }


            @Nested
            @DisplayName("when a box is completely inside the frustum")
            class AABBInsideFrustum {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(2, -1, 1), new Point(3, 1.5, 1.5));
                }

                @Test
                @DisplayName("then the box should be fully visible")
                void fullyVisible() {
                    assertEquals(FULLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box is outside the frustum")
            class AABBOutsideFrustum {

                @BeforeEach
                void setup() {
                    // behind every plane except the top plane
                    box = new ConcreteAABB(new Point(-2, 2.5, -3), new Point(-1, 4, 0));
                }

                @Test
                @DisplayName("then the box should not be visible")
                void notVisible() {
                    assertEquals(NOT_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects the frustum")
            class AABBIntersectsFrustum {

                @BeforeEach
                void setup() {
                    // intersects only the right plane
                    box = new ConcreteAABB(new Point(2, -3, 1), new Point(3, -1.5, 2));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box's side lays in a frustum plane but intersects no other planes")
            class AABBSideLaysInAPlane {

                @BeforeEach
                void setup() {
                    // half the box's vertices lay on the left plane but intersect no other planes
                    box = new ConcreteAABB(new Point(1, 2, 0), new Point(2, 3, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects two frustum planes")
            class AABBIntersectsTwoPlanes {

                @BeforeEach
                void setup() {
                    // intersects the left plane and the back plane in the back-left corner
                    box = new ConcreteAABB(new Point(-0.5, -3, 1), new Point(0.5, -1, 1.1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when the frustum is completely inside the box")
            class FrustumInAABB {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-2, -3, -2), new Point(5, 3, 4));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void notVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }
        }
    }


    @Nested
    @DisplayName("Frusta looking in Y-direction")
    class YDirection {

        @Nested
        @DisplayName("given a 90 degree Frustum")
        class GivenFrustum {

            @BeforeEach
            void setup() {

                // new Plane(Point, normalVector, tolerance)
                Plane top = new Plane(new Vector3D(0, 0, 2), new Vector3D(0, 0, -1), TOLERANCE);
                Plane bottom = new Plane(new Vector3D(0, 0, -1), new Vector3D(0, 0, 1), TOLERANCE);

                // 45° angle
                Plane left = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 1, 0), TOLERANCE);
                // 45° angle
                Plane right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 1, 0), TOLERANCE);

                Plane front = new Plane(new Vector3D(0, 10, 0), new Vector3D(0, -1, 0), TOLERANCE);
                Plane back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0), TOLERANCE);

                frustum = new Frustum(front, back, left, right, top, bottom);
            }

            @Nested
            @DisplayName("when a box is completely inside the frustum")
            class AABBInsideFrustum {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-1, 2, -0.5), new Point(1, 3, 0.5));
                }

                @Test
                @DisplayName("then the box should be fully visible")
                void fullyVisible() {
                    assertEquals(FULLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box is outside the frustum")
            class AABBOutsideFrustum {

                @BeforeEach
                void setup() {
                    // behind every plane except the top plane
                    box = new ConcreteAABB(new Point(2.5, -1, -1.5), new Point(3, -0.5, -1.25));
                }

                @Test
                @DisplayName("then the box should not be visible")
                void notVisible() {
                    assertEquals(NOT_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects the frustum")
            class AABBIntersectsFrustum {

                @BeforeEach
                void setup() {
                    // intersects only the right plane
                    box = new ConcreteAABB(new Point(-4, -1, 0), new Point(-0.5, 1, 1.5));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box's side lays in a frustum plane but intersects no other planes")
            class AABBSideLaysInAPlane {

                @BeforeEach
                void setup() {
                    // half the box's vertices lay on the back plane but intersect no other planes
                    box = new ConcreteAABB(new Point(-1, 0, 0), new Point(1, 1, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }

            }


            @Nested
            @DisplayName("when a box intersects two frustum planes")
            class AABBIntersectsTwoPlanes {

                @BeforeEach
                void setup() {
                    // intersects the left plane and the back plane in the back-left corner
                    box = new ConcreteAABB(new Point(8, 8, 0), new Point(12, 12, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when the frustum is completely inside the box")
            class FrustumInAABB {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-15, -5, -2), new Point(5, 15, 3));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void notVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }
        }


        @Nested
        @DisplayName("given a rectangular shaped Frustum")
        class GivenRectangularFrustum {

            @BeforeEach
            void setup() {

                // new Plane(Point, normalVector, tolerance)
                Plane top = new Plane(new Vector3D(0, 0, 3), new Vector3D(0, 0, -1), TOLERANCE);
                Plane bottom = new Plane(new Vector3D(0, 0, 0.5), new Vector3D(0, 0, 1), TOLERANCE);

                // left and right plane are parallel
                Plane left = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                Plane right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);

                Plane front = new Plane(new Vector3D(0, 4, 0), new Vector3D(0, -1, 0), TOLERANCE);
                Plane back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0), TOLERANCE);

                frustum = new Frustum(front, back, left, right, top, bottom);
            }


            @Nested
            @DisplayName("when a box is completely inside the frustum")
            class AABBInsideFrustum {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-1, 2, 1), new Point(1.5, 3, 1.5));
                }

                @Test
                @DisplayName("then the box should be fully visible")
                void fullyVisible() {
                    assertEquals(FULLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box is outside the frustum")
            class AABBOutsideFrustum {

                @BeforeEach
                void setup() {
                    // behind every plane except the top plane
                    box = new ConcreteAABB(new Point(2.5, -2, -3), new Point(4, -1, 0));
                }

                @Test
                @DisplayName("then the box should not be visible")
                void notVisible() {
                    assertEquals(NOT_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects the frustum")
            class AABBIntersectsFrustum {

                @BeforeEach
                void setup() {
                    // intersects only the right plane
                    box = new ConcreteAABB(new Point(-3, 2, 1), new Point(-1.5, 3, 2));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box's side lays in a frustum plane but intersects no other planes")
            class AABBSideLaysInAPlane {

                @BeforeEach
                void setup() {
                    // half the box's vertices lay on the left plane but intersect no other planes
                    box = new ConcreteAABB(new Point(2, 1, 0), new Point(3, 2, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects two frustum planes")
            class AABBIntersectsTwoPlanes {

                @BeforeEach
                void setup() {
                    // intersects the left plane and the back plane in the back-left corner
                    box = new ConcreteAABB(new Point(-3, -0.5, 1), new Point(-1, 0.5, 1.1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when the frustum is completely inside the box")
            class FrustumInAABB {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-3, -2, -2), new Point(3, 5, 4));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void notVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }
        }
    }


    @Nested
    @DisplayName("Frusta looking in Z-direction")
    class ZDirection {

        @Nested
        @DisplayName("given a 90 degree Frustum")
        class GivenFrustum {

            @BeforeEach
            void setup() {

                // new Plane(Point, normalVector, tolerance)
                Plane top = new Plane(new Vector3D(0, 2, 0), new Vector3D(0, -1, 0), TOLERANCE);
                Plane bottom = new Plane(new Vector3D(0, -1, 0), new Vector3D(0, 1, 0), TOLERANCE);

                // 45° angle
                Plane left = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 0, 1), TOLERANCE);
                // 45° angle
                Plane right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 0, 1), TOLERANCE);

                Plane front = new Plane(new Vector3D(0, 0, 10), new Vector3D(0, 0, -1), TOLERANCE);
                Plane back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 0, 1), TOLERANCE);

                frustum = new Frustum(front, back, left, right, top, bottom);
            }

            @Nested
            @DisplayName("when a box is completely inside the frustum")
            class AABBInsideFrustum {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-1, -0.5, 2), new Point(1, 0.5, 3));
                }

                @Test
                @DisplayName("then the box should be fully visible")
                void fullyVisible() {
                    assertEquals(FULLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box is outside the frustum")
            class AABBOutsideFrustum {

                @BeforeEach
                void setup() {
                    // behind every plane except the top plane
                    box = new ConcreteAABB(new Point(2.5, -1.5, -1), new Point(3, -1.25, -0.5));
                }

                @Test
                @DisplayName("then the box should not be visible")
                void notVisible() {
                    assertEquals(NOT_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects the frustum")
            class AABBIntersectsFrustum {

                @BeforeEach
                void setup() {
                    // intersects only the right plane
                    box = new ConcreteAABB(new Point(-4, 0, -1), new Point(-0.5, 1.5, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box's side lays in a frustum plane but intersects no other planes")
            class AABBSideLaysInAPlane {

                @BeforeEach
                void setup() {
                    // half the box's vertices lay on the back plane but intersect no other planes
                    box = new ConcreteAABB(new Point(-1, 0, 0), new Point(1, 1, 1));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }

            }


            @Nested
            @DisplayName("when a box intersects two frustum planes")
            class AABBIntersectsTwoPlanes {

                @BeforeEach
                void setup() {
                    // intersects the left plane and the back plane in the back-left corner
                    box = new ConcreteAABB(new Point(8, 0, 8), new Point(12, 1, 12));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when the frustum is completely inside the box")
            class FrustumInAABB {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-15, -2, -5), new Point(5, 3, 15));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void notVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }
        }


        @Nested
        @DisplayName("given a rectangular shaped Frustum")
        class GivenRectangularFrustum {

            @BeforeEach
            void setup() {

                // new Plane(Point, normalVector, tolerance)
                Plane top = new Plane(new Vector3D(0, 3, 0), new Vector3D(0, -1, 0), TOLERANCE);
                Plane bottom = new Plane(new Vector3D(0, 0.5, 0), new Vector3D(0, 1, 0), TOLERANCE);

                // left and right plane are parallel
                Plane left = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                Plane right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);

                Plane front = new Plane(new Vector3D(0, 0, 4), new Vector3D(0, 0, -1), TOLERANCE);
                Plane back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 0, 1), TOLERANCE);

                frustum = new Frustum(front, back, left, right, top, bottom);
            }


            @Nested
            @DisplayName("when a box is completely inside the frustum")
            class AABBInsideFrustum {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-1, 1, 2), new Point(1.5, 1.5, 3));
                }

                @Test
                @DisplayName("then the box should be fully visible")
                void fullyVisible() {
                    assertEquals(FULLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box is outside the frustum")
            class AABBOutsideFrustum {

                @BeforeEach
                void setup() {
                    // behind every plane except the top plane
                    box = new ConcreteAABB(new Point(2.5, -3, -2), new Point(4, 0, -1));
                }

                @Test
                @DisplayName("then the box should not be visible")
                void notVisible() {
                    assertEquals(NOT_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects the frustum")
            class AABBIntersectsFrustum {

                @BeforeEach
                void setup() {
                    // intersects only the right plane
                    box = new ConcreteAABB(new Point(-3, 1, 2), new Point(-1.5, 2, 3));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box's side lays in a frustum plane but intersects no other planes")
            class AABBSideLaysInAPlane {

                @BeforeEach
                void setup() {
                    // half the box's vertices lay on the left plane but intersect no other planes
                    box = new ConcreteAABB(new Point(2, 0, 1), new Point(3, 1, 2));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when a box intersects two frustum planes")
            class AABBIntersectsTwoPlanes {

                @BeforeEach
                void setup() {
                    // intersects the left plane and the back plane in the back-left corner
                    box = new ConcreteAABB(new Point(-3, 1, -0.5), new Point(-1, 2, 0.5));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void partlyVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }


            @Nested
            @DisplayName("when the frustum is completely inside the box")
            class FrustumInAABB {

                @BeforeEach
                void setup() {
                    box = new ConcreteAABB(new Point(-3, -2, -2), new Point(3, 4, 5));
                }

                @Test
                @DisplayName("then the box should be partly visible")
                void notVisible() {
                    assertEquals(PARTLY_VISIBLE, frustum.calcVisibility(box));
                }
            }
        }
    }


    @Nested
    @DisplayName("given a Frustum with switched left and right planes ")
    class GivenFrustumLeftRightSwitched {

    }


    @Nested
    @DisplayName("Frusta which planes' normals point outwards instead of inwards")
    class FrustumNormalOutwards {

        Plane top;
        Plane bottom;
        Plane left;
        Plane right;
        Plane front;
        Plane back;


        @Nested
        @DisplayName("Frusta looking in X-Dir")
        class XDirection {

            @Nested
            @DisplayName("given a rectangular shaped frustum looking in X-direction")
            class GivenRectangularFrustum {

                /**
                 * Set up the frustum correctly with all normals pointing inside. The plane values are overwritten to
                 * error values in the corresponding subclasses values
                 */
                @BeforeEach
                void setup() {
                    // correctly instantiated Frustum with all normals pointing inwards
                    // new Plane(Point, normalVector, tolerance)
                    top = new Plane(new Vector3D(0, 0, 3), new Vector3D(0, 0, -1), TOLERANCE);
                    bottom = new Plane(new Vector3D(0, 0, 0.5), new Vector3D(0, 0, 1), TOLERANCE);

                    // left and right plane are parallel
                    left = new Plane(new Vector3D(0, 2, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    right = new Plane(new Vector3D(0, -2, 0), new Vector3D(0, 1, 0), TOLERANCE);

                    front = new Plane(new Vector3D(4, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    back = new Plane(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);
                }

                @Nested
                @DisplayName("normal of the top plane points outwards")
                class TopOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the top plane points outwards
                        top = new Plane(new Vector3D(0, 0, 3), new Vector3D(0, 0, 1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the bottom plane points outwards")
                class BottomOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the bottom plane points outwards
                        bottom = new Plane(new Vector3D(0, 0, 0.5), new Vector3D(0, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the left plane points outwards")
                class LeftOutwards {

                    @BeforeEach
                    void setup() {
                        // left and right plane are parallel
                        // normal of the left plane points outwards
                        left = new Plane(new Vector3D(0, 2, 0), new Vector3D(0, 1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the right plane points outwards")
                class RightOutwards {

                    @BeforeEach
                    void setup() {
                        // left and right plane are parallel
                        // normal of the right plane points outwards
                        right = new Plane(new Vector3D(0, -2, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the front plane points outwards")
                class FrontOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the front plane points outwards
                        front = new Plane(new Vector3D(4, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the back plane points outwards")
                class BackOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the back plane points outwards
                        back = new Plane(new Vector3D(0, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }
            }


            @Nested
            @DisplayName("given a 90 degree Frustum looking in X-direction")
            class GivenFrustum {

                /**
                 * Set up the frustum correctly with all normals pointing inside. The plane values are overwritten to
                 * error values in the corresponding subclasses values
                 */
                @BeforeEach
                void setup() {

                    // new Plane(Point, normalVector, tolerance)
                    top = new Plane(new Vector3D(0, 0, 2), new Vector3D(0, 0, -1), TOLERANCE);
                    bottom = new Plane(new Vector3D(0, 0, -1), new Vector3D(0, 0, 1), TOLERANCE);

                    // 45° angle
                    left = new Plane(new Vector3D(0, 2, 0), new Vector3D(1, -1, 0), TOLERANCE);
                    // 45° angle
                    right = new Plane(new Vector3D(0, -2, 0), new Vector3D(1, 1, 0), TOLERANCE);

                    front = new Plane(new Vector3D(10, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    back = new Plane(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);
                }


                @Nested
                @DisplayName("normal of the top plane points outwards")
                class TopOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the top plane points outwards
                        top = new Plane(new Vector3D(0, 0, 2), new Vector3D(0, 0, 1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the bottom plane points outwards")
                class BottomOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the bottom plane points outwards
                        bottom = new Plane(new Vector3D(0, 0, -1), new Vector3D(0, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the left plane points outwards")
                class LeftOutwards {

                    @BeforeEach
                    void setup() {
                        // left and right plane are parallel
                        // normal of the left plane points outwards
                        left = new Plane(new Vector3D(0, 2, 0), new Vector3D(1, 1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the right plane points outwards")
                class RightOutwards {

                    @BeforeEach
                    void setup() {
                        // left and right plane are parallel
                        // normal of the right plane points outwards
                        right = new Plane(new Vector3D(0, -2, 0), new Vector3D(1, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the front plane points outwards")
                class FrontOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the front plane points outwards
                        front = new Plane(new Vector3D(10, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the back plane points outwards")
                class BackOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the back plane points outwards
                        back = new Plane(new Vector3D(0, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }
            }
        }


        @Nested
        @DisplayName("Frusta looking in Y-direction")
        class YDirection {

            @Nested
            @DisplayName("given a rectangular shaped frustum")
            class GivenRectangularFrustum {

                /**
                 * Set up the frustum correctly with all normals pointing inside. The plane values are overwritten to
                 * error values in the corresponding subclasses
                 */
                @BeforeEach
                void setup() {
                    // new Plane(Point, normalVector, tolerance)
                    top = new Plane(new Vector3D(0, 0, 3), new Vector3D(0, 0, -1), TOLERANCE);
                    bottom = new Plane(new Vector3D(0, 0, 0.5), new Vector3D(0, 0, 1), TOLERANCE);

                    // left and right plane are parallel
                    left = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);

                    front = new Plane(new Vector3D(0, 4, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0), TOLERANCE);
                }

                @Nested
                @DisplayName("normal of the top plane points outwards")
                class TopOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the top plane points outwards
                        top = new Plane(new Vector3D(0, 0, 3), new Vector3D(0, 0, 1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the bottom plane points outwards")
                class BottomOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the bottom plane points outwards
                        bottom = new Plane(new Vector3D(0, 0, 0.5), new Vector3D(0, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the left plane points outwards")
                class LeftOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the left plane points outwards
                        left = new Plane(new Vector3D(2, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the right plane points outwards")
                class RightOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the right plane points outwards
                        right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the front plane points outwards")
                class FrontOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the front plane points outwards
                        front = new Plane(new Vector3D(0, 4, 0), new Vector3D(0, 1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the back plane points outwards")
                class BackOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the back plane points outwards
                        back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }
            }


            @Nested
            @DisplayName("given a 90 degree Frustum looking in X-direction")
            class GivenFrustum {

                /**
                 * Set up the frustum correctly with all normals pointing inside. The plane values are overwritten to
                 * error values in the corresponding subclasses
                 */
                @BeforeEach
                void setup() {

                    // new Plane(Point, normalVector, tolerance)
                    top = new Plane(new Vector3D(0, 0, 2), new Vector3D(0, 0, -1), TOLERANCE);
                    bottom = new Plane(new Vector3D(0, 0, -1), new Vector3D(0, 0, 1), TOLERANCE);

                    // 45° angle
                    left = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 1, 0), TOLERANCE);
                    // 45° angle
                    right = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 1, 0), TOLERANCE);

                    front = new Plane(new Vector3D(0, 10, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0), TOLERANCE);
                }


                @Nested
                @DisplayName("normal of the top plane points outwards")
                class TopOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the top plane points outwards
                        top = new Plane(new Vector3D(0, 0, 2), new Vector3D(0, 0, 1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the bottom plane points outwards")
                class BottomOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the bottom plane points outwards
                        bottom = new Plane(new Vector3D(0, 0, -1), new Vector3D(0, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the left plane points outwards")
                class LeftOutwards {

                    @BeforeEach
                    void setup() {
                        // left and right plane are parallel
                        // normal of the left plane points outwards
                        left = new Plane(new Vector3D(-2, 0, 0), new Vector3D(-1, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the right plane points outwards")
                class RightOutwards {

                    @BeforeEach
                    void setup() {
                        // left and right plane are parallel
                        // normal of the right plane points outwards
                        right = new Plane(new Vector3D(2, 0, 0), new Vector3D(1, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the front plane points outwards")
                class FrontOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the front plane points outwards
                        front = new Plane(new Vector3D(0, 10, 0), new Vector3D(0, 1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the back plane points outwards")
                class BackOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the back plane points outwards
                        back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }
            }
        }


        @Nested
        @DisplayName("Frusta looking in Z-direction")
        class ZDirection {

            @Nested
            @DisplayName("given a rectangular shaped frustum")
            class GivenRectangularFrustum {

                /**
                 * Set up the frustum correctly with all normals pointing inside. The plane values are overwritten to
                 * error values in the corresponding subclasses
                 */
                @BeforeEach
                void setup() {
                    // new Plane(Point, normalVector, tolerance)
                    top = new Plane(new Vector3D(0, 3, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    bottom = new Plane(new Vector3D(0, 0.5, 0), new Vector3D(0, 1, 0), TOLERANCE);

                    // left and right plane are parallel
                    left = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);

                    front = new Plane(new Vector3D(0, 0, 4), new Vector3D(0, 0, -1), TOLERANCE);
                    back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 0, 1), TOLERANCE);
                }

                @Nested
                @DisplayName("normal of the top plane points outwards")
                class TopOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the top plane points outwards
                        top = new Plane(new Vector3D(0, 3, 0), new Vector3D(0, 1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the bottom plane points outwards")
                class BottomOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the bottom plane points outwards
                        bottom = new Plane(new Vector3D(0, 0.5, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the left plane points outwards")
                class LeftOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the left plane points outwards
                        left = new Plane(new Vector3D(2, 0, 0), new Vector3D(1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the right plane points outwards")
                class RightOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the right plane points outwards
                        right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(-1, 0, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the front plane points outwards")
                class FrontOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the front plane points outwards
                        front = new Plane(new Vector3D(0, 0, 4), new Vector3D(0, 0, 1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the back plane points outwards")
                class BackOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the back plane points outwards
                        back = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }
            }


            @Nested
            @DisplayName("given a 90 degree Frustum looking in X-direction")
            class GivenFrustum {

                /**
                 * Set up the frustum correctly with all normals pointing inside. The plane values are overwritten to
                 * error values in the corresponding subclasses
                 */
                @BeforeEach
                void setup() {
                    // new Plane(Point, normalVector, tolerance)
                    top = new Plane(new Vector3D(0, 2, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    bottom = new Plane(new Vector3D(0, -1, 0), new Vector3D(0, 1, 0), TOLERANCE);

                    // 45° angle
                    left = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 0, 1), TOLERANCE);
                    // 45° angle
                    right = new Plane(new Vector3D(-2, 0, 0), new Vector3D(1, 0, 1), TOLERANCE);

                    front = new Plane(new Vector3D(0, 0, 10), new Vector3D(0, 0, -1), TOLERANCE);
                    back = new Plane(new Vector3D(0, 0, -3), new Vector3D(0, 0, 1), TOLERANCE);
                }


                @Nested
                @DisplayName("normal of the top plane points outwards")
                class TopOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the top plane points outwards
                        top = new Plane(new Vector3D(0, 2, 0), new Vector3D(0, 1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the bottom plane points outwards")
                class BottomOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the bottom plane points outwards
                        bottom = new Plane(new Vector3D(0, -1, 0), new Vector3D(0, -1, 0), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the left plane points outwards")
                class LeftOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the left plane points outwards
                        left = new Plane(new Vector3D(2, 0, 0), new Vector3D(1, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the right plane points outwards")
                class RightOutwards {

                    @BeforeEach
                    void setup() {
                        // left and right plane are parallel
                        // normal of the right plane points outwards
                        right = new Plane(new Vector3D(2, 0, 0), new Vector3D(-1, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the front plane points outwards")
                class FrontOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the front plane points outwards
                        front = new Plane(new Vector3D(0, 0, 10), new Vector3D(0, 0, 1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }


                @Nested
                @DisplayName("normal of the back plane points outwards")
                class BackOutwards {

                    @BeforeEach
                    void setup() {
                        // normal of the back plane points outwards
                        back = new Plane(new Vector3D(0, 0, -3), new Vector3D(0, 0, -1), TOLERANCE);
                    }

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void illegalArgument() {
                        assertThrows(IllegalArgumentException.class,
                                () -> new Frustum(front, back, left, right, top, bottom));
                    }
                }
            }
        }
    }
}