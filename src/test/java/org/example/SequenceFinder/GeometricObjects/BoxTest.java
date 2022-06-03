package org.example.SequenceFinder.GeometricObjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoxTest {

    /**
     * Concrete Box Implementation. <br>
     * Used to test the abstract Box class
     */
    static class ConcreteBox extends Box {
        public ConcreteBox(Point vertA, Point vertB) {
            super(vertA, vertB);
        }
    }

    /**
     * Assert that all expected values are port of the actual collection, and that the actual collection contains only
     * the expected values.
     *
     * @param expected the expected values
     * @param actual   the actual values
     */
    private void assertContainsExactly(ArrayList<Point> expected, ArrayList<Point> actual) {
        for (Point expectedContent : expected) {
            assertTrue(actual.contains(expectedContent));
        }

        for (Point actualContent : actual) {
            assertTrue(expected.contains(actualContent));
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
            Point frontUpperLeft = new Point(0, 1, 0);
            Point frontUpperRight = new Point(1, 1, 0);
            Point backLowerLeft = new Point(0, 0, 1);
            Point backLowerRight = new Point(1, 0, 1);
            Point backUpperLeft = new Point(0, 1, 1);
            Point backUpperRight = new Point(1, 1, 1);

            ArrayList<Point> expectedVertices = new ArrayList<>();
            expectedVertices.add(frontLowerLeft);
            expectedVertices.add(frontLowerRight);
            expectedVertices.add(frontUpperLeft);
            expectedVertices.add(frontUpperRight);
            expectedVertices.add(backLowerLeft);
            expectedVertices.add(backLowerRight);
            expectedVertices.add(backUpperLeft);
            expectedVertices.add(backUpperRight);

            assertContainsExactly(expectedVertices, box.getVertices());
        }
    }


    @Nested
    @DisplayName("given a Box(1,2,3)(-1,-2,-3)")
    class Box123Neg1Neg2Neg3 {
        Box box = new ConcreteBox(new Point(1, 2, 3), new Point(-1, -2, -3));

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
            Point frontUpperLeft = new Point(-1, 2, -3);
            Point frontUpperRight = new Point(1, 2, -3);
            Point backLowerLeft = new Point(-1, -2, 3);
            Point backLowerRight = new Point(1, -2, 3);
            Point backUpperLeft = new Point(-1, 2, 3);
            Point backUpperRight = new Point(1, 2, 3);

            ArrayList<Point> expectedVertices = new ArrayList<>();
            expectedVertices.add(frontLowerLeft);
            expectedVertices.add(frontLowerRight);
            expectedVertices.add(frontUpperLeft);
            expectedVertices.add(frontUpperRight);
            expectedVertices.add(backLowerLeft);
            expectedVertices.add(backLowerRight);
            expectedVertices.add(backUpperLeft);
            expectedVertices.add(backUpperRight);

            assertContainsExactly(expectedVertices, box.getVertices());
        }
    }


    @Nested
    @DisplayName("given a Box(-1,-2,-3)(1,2,3)")
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
        @DisplayName("then the vertices should be at (-1, -2, -3), (1, -2, -3), (-1, 2, -3), (1, 2, -3), " +
                "(-1, -2, 3), (1, -2, 3), (-1, 2, 3), (1, 2, 3)")
        void vertices() {
            Point frontLowerLeft = new Point(-1, -2, -3);
            Point frontLowerRight = new Point(1, -2, -3);
            Point frontUpperLeft = new Point(-1, 2, -3);
            Point frontUpperRight = new Point(1, 2, -3);
            Point backLowerLeft = new Point(-1, -2, 3);
            Point backLowerRight = new Point(1, -2, 3);
            Point backUpperLeft = new Point(-1, 2, 3);
            Point backUpperRight = new Point(1, 2, 3);

            ArrayList<Point> expectedVertices = new ArrayList<>();
            expectedVertices.add(frontLowerLeft);
            expectedVertices.add(frontLowerRight);
            expectedVertices.add(frontUpperLeft);
            expectedVertices.add(frontUpperRight);
            expectedVertices.add(backLowerLeft);
            expectedVertices.add(backLowerRight);
            expectedVertices.add(backUpperLeft);
            expectedVertices.add(backUpperRight);

            assertContainsExactly(expectedVertices, box.getVertices());
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
            Point frontUpperLeft = new Point(-1, -0.5, -3);
            Point frontUpperRight = new Point(-0.5, -0.5, -3);
            Point backLowerLeft = new Point(-1, -2, -0.5);
            Point backLowerRight = new Point(-0.5, -2, -0.5);
            Point backUpperLeft = new Point(-1, -0.5, -0.5);
            Point backUpperRight = new Point(-0.5, -0.5, -0.5);

            ArrayList<Point> expectedVertices = new ArrayList<>();
            expectedVertices.add(frontLowerLeft);
            expectedVertices.add(frontLowerRight);
            expectedVertices.add(frontUpperLeft);
            expectedVertices.add(frontUpperRight);
            expectedVertices.add(backLowerLeft);
            expectedVertices.add(backLowerRight);
            expectedVertices.add(backUpperLeft);
            expectedVertices.add(backUpperRight);

            assertContainsExactly(expectedVertices, box.getVertices());
        }
    }


    @Nested
    @DisplayName("given a Box(0.1948, 1834.356, -91.5)(5.5, 85.8984, 0.0000000000001)")
    class Box019481834356Neg915558589840 {
        Box box = new ConcreteBox(new Point(0.1948, 1834.356, -91.5),
                new Point(5.5, 85.8984, 0.0000000000001));

        @Test
        @DisplayName("then the center should be at (2.8474, 960.1272, -45.74999999999995)")
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
        @DisplayName("then the vertices should be at (0.1948, 1834.356, -91.5), (5.5, 1834.356, -91.5), " +
                "(0.1948, 85.8984, -91.5), (5.5, 85.8984, -91.5), (0.1948, 1834.356, 0.0000000000001) " +
                "(5.5, 1834.356, 0.0000000000001), (0.1948, 85.8984, 0.0000000000001) " +
                "(5.5, 85.8984, 0.0000000000001)")
        void vertices() {
            Point frontLowerLeft = new Point(0.1948, 1834.356, -91.5);
            Point frontLowerRight = new Point(5.5, 1834.356, -91.5);
            Point frontUpperLeft = new Point(0.1948, 85.8984, -91.5);
            Point frontUpperRight = new Point(5.5, 85.8984, -91.5);
            Point backLowerLeft = new Point(0.1948, 1834.356, 0.0000000000001);
            Point backLowerRight = new Point(5.5, 1834.356, 0.0000000000001);
            Point backUpperLeft = new Point(0.1948, 85.8984, 0.0000000000001);
            Point backUpperRight = new Point(5.5, 85.8984, 0.0000000000001);

            ArrayList<Point> expectedVertices = new ArrayList<>();
            expectedVertices.add(frontLowerLeft);
            expectedVertices.add(frontLowerRight);
            expectedVertices.add(frontUpperLeft);
            expectedVertices.add(frontUpperRight);
            expectedVertices.add(backLowerLeft);
            expectedVertices.add(backLowerRight);
            expectedVertices.add(backUpperLeft);
            expectedVertices.add(backUpperRight);

            assertContainsExactly(expectedVertices, box.getVertices());
        }
    }


    @Nested
    @DisplayName("given a Box((-2.3, 2.5, 1.3), (1.0, -1.2, -0.66))")
    class BoxNeg23251310Neg12Neg066 {
        Box box = new ConcreteBox(new Point(-2.3, 2.5, 1.3), new Point(1.0, -1.2, -0.66));

        @Test
        @DisplayName("then the center should be at (-0.65, 0.65, 0.32)")
        void center() {
            assertEquals(new Point(-0.65, 0.65, 0.32), box.calcCenter());
        }

        @Test
        @DisplayName("then the radius should be 1.6")
        void radius() {
            assertEquals(1.85, box.calcRadius());
        }

        // ((-2.3, 2.5, 1.3), (1.0, -1.2, -0.66))
        @Test
        @DisplayName("then the vertices should be at (-2.3, 2.5, 1.3), (1.0, 2.5, 1.3), (-2.3, -1.2, 1.3), " +
                "(1.0, -1.2, 1.3), (-2.3, 2.5, -0.66), (1.0, 2.5, -0.66), (-2.3, -1.2, -0.66), (1.0, -1.2, -0.66)")
        void vertices() {
            Point frontLowerLeft = new Point(-2.3, 2.5, 1.3);
            Point frontLowerRight = new Point(1.0, 2.5, 1.3);
            Point frontUpperLeft = new Point(-2.3, -1.2, 1.3);
            Point frontUpperRight = new Point(1.0, -1.2, 1.3);
            Point backLowerLeft = new Point(-2.3, 2.5, -0.66);
            Point backLowerRight = new Point(1.0, 2.5, -0.66);
            Point backUpperLeft = new Point(-2.3, -1.2, -0.66);
            Point backUpperRight = new Point(1.0, -1.2, -0.66);

            ArrayList<Point> expectedVertices = new ArrayList<>();
            expectedVertices.add(frontLowerLeft);
            expectedVertices.add(frontLowerRight);
            expectedVertices.add(frontUpperLeft);
            expectedVertices.add(frontUpperRight);
            expectedVertices.add(backLowerLeft);
            expectedVertices.add(backLowerRight);
            expectedVertices.add(backUpperLeft);
            expectedVertices.add(backUpperRight);

            assertContainsExactly(expectedVertices, box.getVertices());
        }
    }
}