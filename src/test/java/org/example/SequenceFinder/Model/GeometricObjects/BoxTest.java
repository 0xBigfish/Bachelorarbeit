package org.example.SequenceFinder.Model.GeometricObjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }


    @Nested
    @DisplayName("given a Box(0.1948, 1834.356, -91.5)(5.5, 85.8984, 0.0000000000001)")
    class Box019481834356Neg915558589840 {
        Box box = new ConcreteBox(new Point(0.1948, 1834.356, -91.5),
                new Point(5.5, 85.8984, 0.0000000000001));

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
        @DisplayName("then the radius should be 1.85")
        void radius() {
            assertEquals(1.85, box.calcRadius());
        }
    }


    @Nested
    @DisplayName("given a Box((1.6666, 0.001, 1.3), (2.45, 3.4, 0.98))")
    class Box1666600011324534098 {
        Box box = new ConcreteBox(new Point(1.6666, 0.001, 1.3), new Point(2.45, 3.4, 0.98));

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
    }
}