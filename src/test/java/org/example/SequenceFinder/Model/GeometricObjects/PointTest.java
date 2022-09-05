package org.example.SequenceFinder.Model.GeometricObjects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointTest {

    Point point;


    @Nested
    @DisplayName("given a Point (1,2,3)")
    class Point123 {


        @BeforeEach
        void setup() {
            point = new Point(1, 2, 3);
        }

        @Test
        @DisplayName("when converting to Vector3D then the result should be (1,2,3)")
        void vector3D() {
            assertEquals(new Vector3D(1, 2, 3), point.toVector3D());
        }

        @Test
        @DisplayName("then the Point should be equal to another Point (1,2,3)")
        void equalItself() {
            assertEquals(new Point(1, 2, 3), point);
        }
    }


    @Nested
    @DisplayName("given a Point (0.184828472, 1238135423, -0.01")
    class PointDouble {

        @BeforeEach
        void setup() {
            point = new Point(0.184828472, 1238135423, -0.01);
        }

        @Test
        @DisplayName("when converting to Vector3D then the result should be (0.1848, 1238135423, -0.01)")
        void vector3D() {
            assertEquals(new Vector3D(0.184828472, 1238135423, -0.01), point.toVector3D());
        }

        @Test
        @DisplayName("then the Point should be equal to another Point (0.1848, 1238135423, -0.01)")
        void equalItself() {
            assertEquals(new Point(0.184828472, 1238135423, -0.01), point);
        }
    }
}
