package org.example.SequenceFinder.Model.GeometricObjects;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.example.SequenceFinder.OperatingDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.example.SequenceFinder.Model.GeometricObjects.Box.BoxVertex.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoxTest {

    HashMap<Box.BoxVertex, Point> expectedVertices = new HashMap<>();
    /**
     * The tolerance below which points on a plane are considered identical
     */
    double tolerance = 1e-10;


    /**
     * Concrete Box Implementation. <br> Used to test the abstract Box class
     */
    static class ConcreteBox extends Box {
        public ConcreteBox(Point vertA, Point vertB) {
            super(vertA, vertB);
        }
    }


    @Nested
    @DisplayName("given a Box((0,0,0)(1,1,1))")
    class Box000111 {
        Box box = new ConcreteBox(new Point(0, 0, 0), new Point(1, 1, 1));

        @Test
        @DisplayName("then the center should be at (0.5, 0.5, 0.5)")
        void center() {
            assertEquals(new Point(0.5, 0.5, 0.5), box.calcCenter());
        }

        @Test
        @DisplayName("then radius should be 0.5")
        void radius() {
            assertEquals(0.5, box.calcRadius());
        }

        @Test
        @DisplayName("then the vertices should be at (0,0,0), (1,0,0), (0,1,0), (1,1,0), " +
                "(0,0,1), (1,0,1), (0,1,1), (1,1,1)")
        void vertices() {
            Point frontLowerLeft = new Point(0, 0, 0);
            Point frontLowerRight = new Point(1, 0, 0);
            Point frontUpperLeft = new Point(0, 0, 1);
            Point frontUpperRight = new Point(1, 0, 1);
            Point backLowerLeft = new Point(0, 1, 0);
            Point backLowerRight = new Point(1, 1, 0);
            Point backUpperLeft = new Point(0, 1, 1);
            Point backUpperRight = new Point(1, 1, 1);

            expectedVertices.put(FRONT_BOTTOM_LEFT, frontLowerLeft);
            expectedVertices.put(FRONT_BOTTOM_RIGHT, frontLowerRight);
            expectedVertices.put(FRONT_TOP_LEFT, frontUpperLeft);
            expectedVertices.put(FRONT_TOP_RIGHT, frontUpperRight);
            expectedVertices.put(BACK_BOTTOM_LEFT, backLowerLeft);
            expectedVertices.put(BACK_BOTTOM_RIGHT, backLowerRight);
            expectedVertices.put(BACK_TOP_LEFT, backUpperLeft);
            expectedVertices.put(BACK_TOP_RIGHT, backUpperRight);

            assertEquals(expectedVertices, box.getVertices());
        }

        @Nested
        @DisplayName("getSide() tests")
        class Sides {
            @Test
            @DisplayName("then the front side plane should contain (0,0,0) with normal (0,-1,0)")
            void frontSide() {
                Plane frontPlane = new Plane(new Vector3D(0, 0, 0), new Vector3D(0, -1, 0), tolerance);

                assertAll(
                        () -> assertEquals(frontPlane.getOrigin(), box.getSide(OperatingDirection.FRONT).getOrigin()),
                        () -> assertEquals(frontPlane.getNormal(), box.getSide(OperatingDirection.FRONT).getNormal())
                );
            }

            @Test
            @DisplayName("then the back side plane should contain (0,1,0) with normal (0,1,0)")
            void backside() {
                Plane backPlane = new Plane(new Vector3D(0, 1, 0), new Vector3D(0, 1, 0), tolerance);

                assertAll(
                        () -> assertEquals(backPlane.getOrigin(), box.getSide(OperatingDirection.BACK).getOrigin()),
                        () -> assertEquals(backPlane.getNormal(), box.getSide(OperatingDirection.BACK).getNormal())
                );
            }

            @Test
            @DisplayName("then the left side plane should contain (0,0,1) with normal (-1,0,0)")
            void leftSide() {
                Plane leftPlane = new Plane(new Vector3D(0, 0, 1), new Vector3D(-1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(leftPlane.getOrigin(), box.getSide(OperatingDirection.LEFT).getOrigin()),
                        () -> assertEquals(leftPlane.getNormal(), box.getSide(OperatingDirection.LEFT).getNormal())
                );
            }

            @Test
            @DisplayName("the the right side plane should contain (1,0,0) with normal (1,0,0)")
            void rightSide() {
                Plane rightPlane = new Plane(new Vector3D(1, 0, 0), new Vector3D(1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(rightPlane.getOrigin(), box.getSide(OperatingDirection.RIGHT).getOrigin()),
                        () -> assertEquals(rightPlane.getNormal(), box.getSide(OperatingDirection.RIGHT).getNormal())
                );
            }

            @Test
            @DisplayName("then the top side plane should contain (1,1,1) with normal (0,0,1)")
            void topSide() {
                Plane topPlane = new Plane(new Vector3D(1, 1, 1), new Vector3D(0, 0, 1), tolerance);

                assertAll(
                        () -> assertEquals(topPlane.getOrigin(), box.getSide(OperatingDirection.TOP).getOrigin()),
                        () -> assertEquals(topPlane.getNormal(), box.getSide(OperatingDirection.TOP).getNormal())
                );
            }
        }
    }


    @Nested
    @DisplayName("given a Box(-1, -2, -3)(1, 2, 3)")
    class BoxNeg1Neg2Neg3123 {
        Box box = new ConcreteBox(new Point(-1, -2, -3), new Point(1, 2, 3));

        @Test
        @DisplayName("then the center should be at (0, 0, 0)")
        void center() {
            assertEquals(new Point(0, 0, 0), box.calcCenter());
        }

        @Test
        @DisplayName("then the radius should be 3")
        void radius() {
            assertEquals(3, box.calcRadius());
        }

        @Test
        @DisplayName("then the vertices should be at (-1, -2, -3), (1, -2, -3), (-1, 2, -3), (1, 2, -3) " +
                "(-1, -2, 3), (1, -2, 3), (-1, 2, 3), (1, 2, 3)")
        void vertices() {
            Point frontLowerLeft = new Point(-1, -2, -3);
            Point frontLowerRight = new Point(1, -2, -3);
            Point frontUpperLeft = new Point(-1, -2, 3);
            Point frontUpperRight = new Point(1, -2, 3);
            Point backLowerLeft = new Point(-1, 2, -3);
            Point backLowerRight = new Point(1, 2, -3);
            Point backUpperLeft = new Point(-1, 2, 3);
            Point backUpperRight = new Point(1, 2, 3);

            expectedVertices.put(FRONT_BOTTOM_LEFT, frontLowerLeft);
            expectedVertices.put(FRONT_BOTTOM_RIGHT, frontLowerRight);
            expectedVertices.put(FRONT_TOP_LEFT, frontUpperLeft);
            expectedVertices.put(FRONT_TOP_RIGHT, frontUpperRight);
            expectedVertices.put(BACK_BOTTOM_LEFT, backLowerLeft);
            expectedVertices.put(BACK_BOTTOM_RIGHT, backLowerRight);
            expectedVertices.put(BACK_TOP_LEFT, backUpperLeft);
            expectedVertices.put(BACK_TOP_RIGHT, backUpperRight);

            assertEquals(expectedVertices, box.getVertices());
        }

        @Nested
        @DisplayName("getSide() tests")
        class Sides {
            @Test
            @DisplayName("then the front side plane should contain (-1,-2,-3) with normal (0,-1,0)")
            void frontSide() {
                Plane frontPlane = new Plane(new Vector3D(-1, -2, -3), new Vector3D(0, -1, 0), tolerance);

                assertAll(
                        () -> assertEquals(frontPlane.getOrigin(), box.getSide(OperatingDirection.FRONT).getOrigin()),
                        () -> assertEquals(frontPlane.getNormal(), box.getSide(OperatingDirection.FRONT).getNormal())
                );
            }

            @Test
            @DisplayName("then the back side plane should contain (-1,2,-3) with normal (0,1,0)")
            void backside() {
                Plane backPlane = new Plane(new Vector3D(-1, 2, 3), new Vector3D(0, 1, 0), tolerance);

                assertAll(
                        () -> assertEquals(backPlane.getOrigin(), box.getSide(OperatingDirection.BACK).getOrigin()),
                        () -> assertEquals(backPlane.getNormal(), box.getSide(OperatingDirection.BACK).getNormal())
                );
            }

            @Test
            @DisplayName("then the left side plane should contain (-1,-2,3) with normal (-1,0,0)")
            void leftSide() {
                Plane leftPlane = new Plane(new Vector3D(-1, -2, 3), new Vector3D(-1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(leftPlane.getOrigin(), box.getSide(OperatingDirection.LEFT).getOrigin()),
                        () -> assertEquals(leftPlane.getNormal(), box.getSide(OperatingDirection.LEFT).getNormal())
                );
            }

            @Test
            @DisplayName("the the right side plane should contain (1,-2,3-) with normal (1,0,0)")
            void rightSide() {
                Plane rightPlane = new Plane(new Vector3D(1, -2, -3), new Vector3D(1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(rightPlane.getOrigin(), box.getSide(OperatingDirection.RIGHT).getOrigin()),
                        () -> assertEquals(rightPlane.getNormal(), box.getSide(OperatingDirection.RIGHT).getNormal())
                );
            }

            @Test
            @DisplayName("then the top side plane should contain (1,2,3) with normal (0,0,1)")
            void topSide() {
                Plane topPlane = new Plane(new Vector3D(1, 2, 3), new Vector3D(0, 0, 1), tolerance);

                assertAll(
                        () -> assertEquals(topPlane.getOrigin(), box.getSide(OperatingDirection.TOP).getOrigin()),
                        () -> assertEquals(topPlane.getNormal(), box.getSide(OperatingDirection.TOP).getNormal())
                );
            }
        }
    }


    @Nested
    @DisplayName("given a Box(-1,-2,-3)(-0.5,-0.5,-0.5)")
    class BoxNeg1Neg2Neg3Neg05Neg05Neg05 {
        Box box = new ConcreteBox(new Point(-1, -2, -3), new Point(-0.5, -0.5, -0.5));

        @Test
        @DisplayName("then the center should be at (-0.75, -1.25, -1.75)")
        void center() {
            assertEquals(new Point(-0.75, -1.25, -1.75), box.calcCenter());
        }

        @Test
        @DisplayName("then the radius should be 1.25")
        void radius() {
            assertEquals(1.25, box.calcRadius());
        }

        @Test
        @DisplayName("then the vertices should be at (-1, -2, -3), (-0.5, -2, -3), (-1, -0.5, -3), (-0.5, -0.5, -3), " +
                "(-1, -2, -0.5), (-0.5, -2, -0.5), (-1, -0.5, -0.5), (-0.5, -0.5, -0.5)")
        void vertices() {
            Point frontLowerLeft = new Point(-1, -2, -3);
            Point frontLowerRight = new Point(-0.5, -2, -3);
            Point frontUpperLeft = new Point(-1, -2, -0.5);
            Point frontUpperRight = new Point(-0.5, -2, -0.5);
            Point backLowerLeft = new Point(-1, -0.5, -3);
            Point backLowerRight = new Point(-0.5, -0.5, -3);
            Point backUpperLeft = new Point(-1, -0.5, -0.5);
            Point backUpperRight = new Point(-0.5, -0.5, -0.5);

            expectedVertices.put(FRONT_BOTTOM_LEFT, frontLowerLeft);
            expectedVertices.put(FRONT_BOTTOM_RIGHT, frontLowerRight);
            expectedVertices.put(FRONT_TOP_LEFT, frontUpperLeft);
            expectedVertices.put(FRONT_TOP_RIGHT, frontUpperRight);
            expectedVertices.put(BACK_BOTTOM_LEFT, backLowerLeft);
            expectedVertices.put(BACK_BOTTOM_RIGHT, backLowerRight);
            expectedVertices.put(BACK_TOP_LEFT, backUpperLeft);
            expectedVertices.put(BACK_TOP_RIGHT, backUpperRight);

            assertEquals(expectedVertices, box.getVertices());
        }

        @Nested
        @DisplayName("getSide() tests")
        class Sides {
            @Test
            @DisplayName("then the front side plane should contain (-1,-2,-3) with normal (0,-1,0)")
            void frontSide() {
                Plane frontPlane = new Plane(new Vector3D(-1, -2, -3), new Vector3D(0, -1, 0), tolerance);

                assertAll(
                        () -> assertEquals(frontPlane.getOrigin(), box.getSide(OperatingDirection.FRONT).getOrigin()),
                        () -> assertEquals(frontPlane.getNormal(), box.getSide(OperatingDirection.FRONT).getNormal())
                );
            }

            @Test
            @DisplayName("then the back side plane should contain (-0.5,-0.5,-0.5) with normal (0,1,0)")
            void backside() {
                Plane backPlane = new Plane(new Vector3D(-0.5, -0.5, -0.5), new Vector3D(0, 1, 0), tolerance);

                assertAll(
                        () -> assertEquals(backPlane.getOrigin(), box.getSide(OperatingDirection.BACK).getOrigin()),
                        () -> assertEquals(backPlane.getNormal(), box.getSide(OperatingDirection.BACK).getNormal())
                );
            }

            @Test
            @DisplayName("then the left side plane should contain (-1,-2,-3) with normal (-1,0,0)")
            void leftSide() {
                Plane leftPlane = new Plane(new Vector3D(-1, -2, -3), new Vector3D(-1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(leftPlane.getOrigin(), box.getSide(OperatingDirection.LEFT).getOrigin()),
                        () -> assertEquals(leftPlane.getNormal(), box.getSide(OperatingDirection.LEFT).getNormal())
                );
            }

            @Test
            @DisplayName("the the right side plane should contain (-0.5,-2,-3) with normal (1,0,0)")
            void rightSide() {
                Plane rightPlane = new Plane(new Vector3D(-0.5, -2, -3), new Vector3D(1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(rightPlane.getOrigin(), box.getSide(OperatingDirection.RIGHT).getOrigin()),
                        () -> assertEquals(rightPlane.getNormal(), box.getSide(OperatingDirection.RIGHT).getNormal())
                );
            }

            @Test
            @DisplayName("then the top side plane should contain (-0.5,-0.5,-0.5) with normal (0,0,1)")
            void topSide() {
                Plane topPlane = new Plane(new Vector3D(-0.5, -0.5, -0.5), new Vector3D(0, 0, 1), tolerance);

                assertAll(
                        () -> assertEquals(topPlane.getOrigin(), box.getSide(OperatingDirection.TOP).getOrigin()),
                        () -> assertEquals(topPlane.getNormal(), box.getSide(OperatingDirection.TOP).getNormal())
                );
            }
        }
    }


    @Nested
    @DisplayName("given a Box(0.1948, 85.8984, -91.5)(5.5, 1834.356, 0.0000000000001)")
    class Box019481834356Neg915558589840 {
        Box box = new ConcreteBox(new Point(0.1948, 85.8984, -91.5),
                new Point(5.5, 1834.356, 0.0000000000001));

        @Test
        @DisplayName("then the center should be at (2.8474, 960.1272, -45.75)")
        void center() {
            assertEquals(new Point(2.8474, 960.1272, -45.75), box.calcCenter());
        }

        @Test
        @DisplayName("then the radius should be 874.228")
        void calcRadius() {
            assertEquals(874.2288, box.calcRadius());
        }

        // given  a box Box(0.1948, 1834.356, -91.5)(5.5, 85.8984, 0.0000000000001)
        @Test
        @DisplayName("then the vertices should be at (0.1948, 85.8984, -91.5), (5.5, 85.8984, -91.5), " +
                "(0.1948, 1834.356, -91.5), (5.5, 1834.356, -91.5), (0.1948, 85.8984, 0.0000000000001) " +
                "(5.5, 85.8984, 0.0000000000001), (0.1948, 1834.356, 0.0000000000001) " +
                "(5.5, 1834.356, 0.0000000000001)")
        void vertices() {
            // (0.1948, 85.8984, -91.5)(5.5, 1834.356, 0.0000000000001)
            Point frontLowerLeft = new Point(0.1948, 85.8984, -91.5);
            Point frontLowerRight = new Point(5.5, 85.8984, -91.5);
            Point frontUpperLeft = new Point(0.1948, 85.8984, 0.0000000000001);
            Point frontUpperRight = new Point(5.5, 85.8984, 0.0000000000001);
            Point backLowerLeft = new Point(0.1948, 1834.356, -91.5);
            Point backLowerRight = new Point(5.5, 1834.356, -91.5);
            Point backUpperLeft = new Point(0.1948, 1834.356, 0.0000000000001);
            Point backUpperRight = new Point(5.5, 1834.356, 0.0000000000001);

            expectedVertices.put(FRONT_BOTTOM_LEFT, frontLowerLeft);
            expectedVertices.put(FRONT_BOTTOM_RIGHT, frontLowerRight);
            expectedVertices.put(FRONT_TOP_LEFT, frontUpperLeft);
            expectedVertices.put(FRONT_TOP_RIGHT, frontUpperRight);
            expectedVertices.put(BACK_BOTTOM_LEFT, backLowerLeft);
            expectedVertices.put(BACK_BOTTOM_RIGHT, backLowerRight);
            expectedVertices.put(BACK_TOP_LEFT, backUpperLeft);
            expectedVertices.put(BACK_TOP_RIGHT, backUpperRight);

            assertEquals(expectedVertices, box.getVertices());
        }

        @Nested
        @DisplayName("getSide() tests")
        class Sides {
            @Test
            @DisplayName("then the front side plane should contain (0.1948, 85.8984, -91.5) with normal (0,-1,0)")
            void frontSide() {
                Plane frontPlane = new Plane(new Vector3D(0.1948, 85.8984, -91.5), new Vector3D(0, -1, 0), tolerance);

                assertAll(
                        () -> assertEquals(frontPlane.getOrigin(), box.getSide(OperatingDirection.FRONT).getOrigin()),
                        () -> assertEquals(frontPlane.getNormal(), box.getSide(OperatingDirection.FRONT).getNormal())
                );
            }

            @Test
            @DisplayName("then the back side plane should contain (0.1948, 1834.356, -91.5) with normal (0,1,0)")
            void backside() {
                Plane backPlane = new Plane(new Vector3D(0.1948, 1834.356, -91.5), new Vector3D(0, 1, 0), tolerance);

                assertAll(
                        () -> assertEquals(backPlane.getOrigin(), box.getSide(OperatingDirection.BACK).getOrigin()),
                        () -> assertEquals(backPlane.getNormal(), box.getSide(OperatingDirection.BACK).getNormal())
                );
            }

            @Test
            @DisplayName("then the left side plane should contain (0.1948, 85.8984, -91.5) with normal (-1,0,0)")
            void leftSide() {
                Plane leftPlane = new Plane(new Vector3D(0.1948, 85.8984, -91.5), new Vector3D(-1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(leftPlane.getOrigin(), box.getSide(OperatingDirection.LEFT).getOrigin()),
                        () -> assertEquals(leftPlane.getNormal(), box.getSide(OperatingDirection.LEFT).getNormal())
                );
            }

            @Test
            @DisplayName("the the right side plane should contain (5.5, 1834.356, 0.0000000000001) with normal (1,0,0)")
            void rightSide() {
                Plane rightPlane = new Plane(
                        new Vector3D(5.5, 1834.356, 0.0000000000001),
                        new Vector3D(1, 0, 0),
                        tolerance);

                assertAll(
                        () -> assertEquals(rightPlane.getOrigin(), box.getSide(OperatingDirection.RIGHT).getOrigin()),
                        () -> assertEquals(rightPlane.getNormal(), box.getSide(OperatingDirection.RIGHT).getNormal())
                );
            }

            @Test
            @DisplayName("then the top side plane should contain (0.1948, 1834.356, 0.0000000000001) with " +
                    "normal (0,0,1)")
            void topSide() {
                Plane topPlane = new Plane(
                        new Vector3D(0.1948, 1834.356, 0.0000000000001),
                        new Vector3D(0, 0, 1),
                        tolerance);

                assertAll(
                        () -> assertEquals(topPlane.getOrigin(), box.getSide(OperatingDirection.TOP).getOrigin()),
                        () -> assertEquals(topPlane.getNormal(), box.getSide(OperatingDirection.TOP).getNormal())
                );
            }
        }
    }


    @Nested
    @DisplayName("given a Box((-2.3, -1.2, -0.66), (1.0, 2.5, 1.3))")
    class BoxNeg23251310Neg12Neg066 {
        Box box = new ConcreteBox(new Point(-2.3, -1.2, -0.66), new Point(1.0, 2.5, 1.3));

        @Test
        @DisplayName("then the center should be at (-0.65, 0.65, 0.32)")
        void center() {
            assertEquals(new Point(-0.65, 0.65, 0.32), box.calcCenter());
        }

        @Test
        @DisplayName("then the radius should be 1.85")
        void radius() {
            assertEquals(1.85, box.calcRadius());
        }

        @Test
        @DisplayName("then the vertices should be at (-2.3, -1.2, -0.66), (1.0, -1.2, -0.66), (-2.3, 2.5, -0.66), " +
                "(1.0, 2.5, -0.66), (-2.3, -1.2, 1.3), (1.0, -1.2, 1.3), (-2.3, 2.5, 1.3), (1.0, 2.5, 1.3)")
        void vertices() {
            Point frontLowerLeft = new Point(-2.3, -1.2, -0.66);
            Point frontLowerRight = new Point(1.0, -1.2, -0.66);
            Point frontUpperLeft = new Point(-2.3, -1.2, 1.3);
            Point frontUpperRight = new Point(1.0, -1.2, 1.3);
            Point backLowerLeft = new Point(-2.3, 2.5, -0.66);
            Point backLowerRight = new Point(1.0, 2.5, -0.66);
            Point backUpperLeft = new Point(-2.3, 2.5, 1.3);
            Point backUpperRight = new Point(1.0, 2.5, 1.3);

            expectedVertices.put(FRONT_BOTTOM_LEFT, frontLowerLeft);
            expectedVertices.put(FRONT_BOTTOM_RIGHT, frontLowerRight);
            expectedVertices.put(FRONT_TOP_LEFT, frontUpperLeft);
            expectedVertices.put(FRONT_TOP_RIGHT, frontUpperRight);
            expectedVertices.put(BACK_BOTTOM_LEFT, backLowerLeft);
            expectedVertices.put(BACK_BOTTOM_RIGHT, backLowerRight);
            expectedVertices.put(BACK_TOP_LEFT, backUpperLeft);
            expectedVertices.put(BACK_TOP_RIGHT, backUpperRight);

            assertEquals(expectedVertices, box.getVertices());
        }

        @Nested
        @DisplayName("getSide() tests")
        class Sides {
            @Test
            @DisplayName("then the front side plane should contain (-2.3, -1.2, -0.66) with normal (0,-1,0)")
            void frontSide() {
                Plane frontPlane = new Plane(new Vector3D(-2.3, -1.2, -0.66), new Vector3D(0, -1, 0), tolerance);

                assertAll(
                        () -> assertEquals(frontPlane.getOrigin(), box.getSide(OperatingDirection.FRONT).getOrigin()),
                        () -> assertEquals(frontPlane.getNormal(), box.getSide(OperatingDirection.FRONT).getNormal())
                );
            }

            @Test
            @DisplayName("then the back side plane should contain (1.0, 2.5, 1.3) with normal (0,1,0)")
            void backside() {
                Plane backPlane = new Plane(new Vector3D(1.0, 2.5, 1.3), new Vector3D(0, 1, 0), tolerance);

                assertAll(
                        () -> assertEquals(backPlane.getOrigin(), box.getSide(OperatingDirection.BACK).getOrigin()),
                        () -> assertEquals(backPlane.getNormal(), box.getSide(OperatingDirection.BACK).getNormal())
                );
            }

            @Test
            @DisplayName("then the left side plane should contain (-2.3, 2.5, 1.3) with normal (-1,0,0)")
            void leftSide() {
                Plane leftPlane = new Plane(new Vector3D(-2.3, 2.5, 1.3), new Vector3D(-1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(leftPlane.getOrigin(), box.getSide(OperatingDirection.LEFT).getOrigin()),
                        () -> assertEquals(leftPlane.getNormal(), box.getSide(OperatingDirection.LEFT).getNormal())
                );
            }

            @Test
            @DisplayName("the the right side plane should contain (1.0, 2.5, -0.66) with normal (1,0,0)")
            void rightSide() {
                Plane rightPlane = new Plane(new Vector3D(1.0, 2.5, 1.3), new Vector3D(1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(rightPlane.getOrigin(), box.getSide(OperatingDirection.RIGHT).getOrigin()),
                        () -> assertEquals(rightPlane.getNormal(), box.getSide(OperatingDirection.RIGHT).getNormal())
                );
            }

            @Test
            @DisplayName("then the top side plane should contain (-2.3, 2.5, 1.3) with normal (0,0,1)")
            void topSide() {
                Plane topPlane = new Plane(new Vector3D(-2.3, 2.5, 1.3), new Vector3D(0, 0, 1), tolerance);

                assertAll(
                        () -> assertEquals(topPlane.getOrigin(), box.getSide(OperatingDirection.TOP).getOrigin()),
                        () -> assertEquals(topPlane.getNormal(), box.getSide(OperatingDirection.TOP).getNormal())
                );
            }
        }
    }


    @Nested
    @DisplayName("given a Box((1.6666, 0.001, 0.98), (2.45, 3.4, 1.3))")
    class Box1666600011324534098 {
        Box box = new ConcreteBox(new Point(1.6666, 0.001, 0.98), new Point(2.45, 3.4, 1.3));

        @Test
        @DisplayName("then the center should be at (2.0583, 1.7005, 1.14)")
        void center() {
            assertEquals(new Point(2.0583, 1.7005, 1.14), box.calcCenter());
        }

        @Test
        @DisplayName("then the radius should be 1.6995")
        void radius() {
            assertEquals(1.6995, box.calcRadius());
        }

        @Test
        @DisplayName("then the vertices should be at (1.6666, 0.001, 0.98), (2.45, 0.001, 0.98), " +
                "(1.6666, 3.4, 0.98), (2.45, 3.4, 0.98), (1.6666, 0.001, 1.3), (2.45, 0.001, 1.3), " +
                "(1.6666, 3.4, 1.3), (2.45, 3.4, 1.3)")
        void vertices() {
            Point frontLowerLeft = new Point(1.6666, 0.001, 0.98);
            Point frontLowerRight = new Point(2.45, 0.001, 0.98);
            Point frontUpperLeft = new Point(1.6666, 0.001, 1.3);
            Point frontUpperRight = new Point(2.45, 0.001, 1.3);
            Point backLowerLeft = new Point(1.6666, 3.4, 0.98);
            Point backLowerRight = new Point(2.45, 3.4, 0.98);
            Point backUpperLeft = new Point(1.6666, 3.4, 1.3);
            Point backUpperRight = new Point(2.45, 3.4, 1.3);

            expectedVertices.put(FRONT_BOTTOM_LEFT, frontLowerLeft);
            expectedVertices.put(FRONT_BOTTOM_RIGHT, frontLowerRight);
            expectedVertices.put(FRONT_TOP_LEFT, frontUpperLeft);
            expectedVertices.put(FRONT_TOP_RIGHT, frontUpperRight);
            expectedVertices.put(BACK_BOTTOM_LEFT, backLowerLeft);
            expectedVertices.put(BACK_BOTTOM_RIGHT, backLowerRight);
            expectedVertices.put(BACK_TOP_LEFT, backUpperLeft);
            expectedVertices.put(BACK_TOP_RIGHT, backUpperRight);

            assertEquals(expectedVertices, box.getVertices());
        }

        @Nested
        @DisplayName("getSide() tests")
        class Sides {
            @Test
            @DisplayName("then the front side plane should contain (1.6666, 0.001, 0.98) with normal (0,-1,0)")
            void frontSide() {
                Plane frontPlane = new Plane(new Vector3D(1.6666, 0.001, 0.98), new Vector3D(0, -1, 0), tolerance);

                assertAll(
                        () -> assertEquals(frontPlane.getOrigin(), box.getSide(OperatingDirection.FRONT).getOrigin()),
                        () -> assertEquals(frontPlane.getNormal(), box.getSide(OperatingDirection.FRONT).getNormal())
                ); //TODO: delete this (1.6666, 0.001, 0.98), (2.45, 3.4, 1.3)
            }

            @Test
            @DisplayName("then the back side plane should contain (2.45, 3.4, 1.3) with normal (0,1,0)")
            void backside() {
                Plane backPlane = new Plane(new Vector3D(2.45, 3.4, 1.3), new Vector3D(0, 1, 0), tolerance);

                assertAll(
                        () -> assertEquals(backPlane.getOrigin(), box.getSide(OperatingDirection.BACK).getOrigin()),
                        () -> assertEquals(backPlane.getNormal(), box.getSide(OperatingDirection.BACK).getNormal())
                );
            }

            @Test
            @DisplayName("then the left side plane should contain (1.6666, 3.4, 1.3) with normal (-1,0,0)")
            void leftSide() {
                Plane leftPlane = new Plane(new Vector3D(1.6666, 3.4, 1.3), new Vector3D(-1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(leftPlane.getOrigin(), box.getSide(OperatingDirection.LEFT).getOrigin()),
                        () -> assertEquals(leftPlane.getNormal(), box.getSide(OperatingDirection.LEFT).getNormal())
                );
            }

            @Test
            @DisplayName("the the right side plane should contain (2.45, 0.001, 0.98) with normal (1,0,0)")
            void rightSide() {
                Plane rightPlane = new Plane(new Vector3D(2.45, 0.001, 0.98), new Vector3D(1, 0, 0), tolerance);

                assertAll(
                        () -> assertEquals(rightPlane.getOrigin(), box.getSide(OperatingDirection.RIGHT).getOrigin()),
                        () -> assertEquals(rightPlane.getNormal(), box.getSide(OperatingDirection.RIGHT).getNormal())
                );
            }

            @Test
            @DisplayName("then the top side plane should contain (1.6666, 0.001, 1.3) with normal (0,0,1)")
            void topSide() {
                Plane topPlane = new Plane(new Vector3D(1.6666, 0.001, 1.3), new Vector3D(0, 0, 1), tolerance);

                assertAll(
                        () -> assertEquals(topPlane.getOrigin(), box.getSide(OperatingDirection.TOP).getOrigin()),
                        () -> assertEquals(topPlane.getNormal(), box.getSide(OperatingDirection.TOP).getNormal())
                );
            }
        }
    }
}