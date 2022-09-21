package org.example.SequenceFinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OperatingDirectionTest {

    OperatingDirection direction;


    @Nested
    @DisplayName("given the FRONT direction")
    class Front {

        @BeforeEach
        void setup() {
            direction = OperatingDirection.FRONT;
        }

        @Test
        @DisplayName("then the opposite direction is BACK")
        void opposite() {
            assertEquals(OperatingDirection.BACK, direction.getOpposite());
        }

        @Test
        @DisplayName("then the left direction is LEFT")
        void left() {
            assertEquals(OperatingDirection.LEFT, direction.getLeft());
        }

        @Test
        @DisplayName("then the right direction is RIGHT")
        void right() {
            assertEquals(OperatingDirection.RIGHT, direction.getRight());
        }

        @Test
        @DisplayName("then the top direction is TOP")
        void top() {
            assertEquals(OperatingDirection.TOP, direction.getTop());
        }

        @Test
        @DisplayName("then the bottom direction is BOTTOM")
        void bottom() {
            assertEquals(OperatingDirection.BOTTOM, direction.getBottom());
        }
    }


    @Nested
    @DisplayName("given the BACK direction")
    class Back {

        @BeforeEach
        void setup() {
            direction = OperatingDirection.BACK;
        }

        @Test
        @DisplayName("then the opposite direction is FRONT")
        void opposite() {
            assertEquals(OperatingDirection.FRONT, direction.getOpposite());
        }

        @Test
        @DisplayName("then the left direction is RIGHT")
        void left() {
            assertEquals(OperatingDirection.RIGHT, direction.getLeft());
        }

        @Test
        @DisplayName("then the right direction is LEFT")
        void right() {
            assertEquals(OperatingDirection.LEFT, direction.getRight());
        }

        @Test
        @DisplayName("then the top direction is TOP")
        void top() {
            assertEquals(OperatingDirection.TOP, direction.getTop());
        }

        @Test
        @DisplayName("then the bottom direction is BOTTOM")
        void bottom() {
            assertEquals(OperatingDirection.BOTTOM, direction.getBottom());
        }
    }


    @Nested
    @DisplayName("given the LEFT direction")
    class Left {

        @BeforeEach
        void setup() {
            direction = OperatingDirection.LEFT;
        }

        @Test
        @DisplayName("then the opposite direction is RIGHT")
        void opposite() {
            assertEquals(OperatingDirection.RIGHT, direction.getOpposite());
        }

        @Test
        @DisplayName("then the left direction is BACK")
        void left() {
            assertEquals(OperatingDirection.BACK, direction.getLeft());
        }

        @Test
        @DisplayName("then the right direction is FRONT")
        void right() {
            assertEquals(OperatingDirection.FRONT, direction.getRight());
        }

        @Test
        @DisplayName("then the top direction is TOP")
        void top() {
            assertEquals(OperatingDirection.TOP, direction.getTop());
        }

        @Test
        @DisplayName("then the bottom direction is BOTTOM")
        void bottom() {
            assertEquals(OperatingDirection.BOTTOM, direction.getBottom());
        }
    }


    @Nested
    @DisplayName("given the RIGHT direction")
    class Right {

        @BeforeEach
        void setup() {
            direction = OperatingDirection.RIGHT;
        }

        @Test
        @DisplayName("then the opposite direction is LEFT")
        void opposite() {
            assertEquals(OperatingDirection.LEFT, direction.getOpposite());
        }

        @Test
        @DisplayName("then the left direction is FRONT")
        void left() {
            assertEquals(OperatingDirection.FRONT, direction.getLeft());
        }

        @Test
        @DisplayName("then the right direction is BACK")
        void right() {
            assertEquals(OperatingDirection.BACK, direction.getRight());
        }

        @Test
        @DisplayName("then the top direction is TOP")
        void top() {
            assertEquals(OperatingDirection.TOP, direction.getTop());
        }

        @Test
        @DisplayName("then the bottom direction is BOTTOM")
        void bottom() {
            assertEquals(OperatingDirection.BOTTOM, direction.getBottom());
        }
    }


    @Nested
    @DisplayName("given the TOP direction")
    class Top {

        @BeforeEach
        void setup() {
            direction = OperatingDirection.TOP;
        }

        @Test
        @DisplayName("then the opposite direction is BOTTOM")
        void opposite() {
            assertEquals(OperatingDirection.BOTTOM, direction.getOpposite());
        }

        @Test
        @DisplayName("then the left direction is LEFT")
        void left() {
            assertEquals(OperatingDirection.LEFT, direction.getLeft());
        }

        @Test
        @DisplayName("then the right direction is RIGHT")
        void right() {
            assertEquals(OperatingDirection.RIGHT, direction.getRight());
        }

        @Test
        @DisplayName("then the top direction is BACK")
        void top() {
            assertEquals(OperatingDirection.BACK, direction.getTop());
        }

        @Test
        @DisplayName("then the bottom direction is FRONT")
        void bottom() {
            assertEquals(OperatingDirection.FRONT, direction.getBottom());
        }
    }


    @Nested
    @DisplayName("given the BOTTOM direction")
    class Bottom {

        @BeforeEach
        void setup() {
            direction = OperatingDirection.BOTTOM;
        }

        @Test
        @DisplayName("then the opposite direction is TOP")
        void opposite() {
            assertEquals(OperatingDirection.TOP, direction.getOpposite());
        }

        @Test
        @DisplayName("then the left direction is LEFT")
        void left() {
            assertEquals(OperatingDirection.LEFT, direction.getLeft());
        }

        @Test
        @DisplayName("then the right direction is RIGHT")
        void right() {
            assertEquals(OperatingDirection.RIGHT, direction.getRight());
        }

        @Test
        @DisplayName("then the top direction is FRONT")
        void top() {
            assertEquals(OperatingDirection.FRONT, direction.getTop());
        }

        @Test
        @DisplayName("then the bottom direction is BACK")
        void bottom() {
            assertEquals(OperatingDirection.BACK, direction.getBottom());
        }
    }

}