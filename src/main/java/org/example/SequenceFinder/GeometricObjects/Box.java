package org.example.SequenceFinder.GeometricObjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

//TODO: rename to AABBox (axis aligned bounding box) and write test to ensure it is axis aligned
/**
 * A box-shaped object described by two vertices.
 */
public abstract class Box {

    Point vertexA;
    Point vertexB;
    /**
     * rounding to 5 decimals should be sufficient for most cases
     */
    int decimalPlaces = 5;

    public Box(Point vertA, Point vertB) {
        this.vertexA = vertA;
        this.vertexB = vertB;
    }

    /**
     * Calculate the center of this Box
     *
     * @return the center of the box rounded to 5 decimals
     */
    public Point calcCenter() {
        double x = round((vertexA.x + vertexB.x) / 2, decimalPlaces);
        double y = round((vertexA.y + vertexB.y) / 2, decimalPlaces);
        double z = round((vertexA.z + vertexB.z) / 2, decimalPlaces);

        return new Point(x, y, z);
    }

    /**
     * Radius of the box. <br>
     * <b>Import note:</b> the radius does not fully enclose the box. It only touches the center of the box's borders
     *
     * @return the radius of the box rounded to 5 decimals
     */
    public double calcRadius() {
        Point center = calcCenter();

        // doesn't matter whether to use vertexA oder vertexB, as the center point has equal distance
        // to both points, and therefore to sides of the
        double result =
                Math.max(
                        Math.max(
                                Math.abs(center.x - vertexA.x),
                                Math.abs(center.y - vertexA.y)
                        ),
                        Math.abs(center.z - vertexA.z)
                );

        return round(result, decimalPlaces);
    }

    /**
     * Get the corner vertices of the box
     *
     * @return the corner vertices of this box
     */
    public ArrayList<Point> getVertices() {
        new Point(vertexA.x + vertexB.x, vertexA.y + vertexB.y, vertexA.z + vertexB.z);
        Point frontLowerLeft = vertexA;
        Point frontLowerRight = new Point(vertexB.x, vertexA.y, vertexA.z);
        Point frontUpperLeft = new Point(vertexA.x, vertexB.y, vertexA.z);
        Point frontUpperRight = new Point(vertexB.x, vertexB.y, vertexA.z);
        Point backLowerLeft = new Point(vertexA.x, vertexA.y, vertexB.z);
        Point backLowerRight = new Point(vertexB.x, vertexA.y, vertexB.z);
        Point backUpperLeft = new Point(vertexA.x, vertexB.y, vertexB.z);
        Point backUpperRight = vertexB;

        ArrayList<Point> vertices = new ArrayList<>();
        vertices.add(frontLowerLeft);
        vertices.add(frontLowerRight);
        vertices.add(frontUpperLeft);
        vertices.add(frontUpperRight);
        vertices.add(backLowerLeft);
        vertices.add(backLowerRight);
        vertices.add(backUpperLeft);
        vertices.add(backUpperRight);

        return vertices;
    }

    /**
     * Round values to prevent float point precision errors. <br>
     * Example: (-2.3 + 1.0) = -0.65 but the calculated result is -0.6499999999999999 <br>
     * <br>
     * Code from https://www.baeldung.com/java-round-decimal-number
     *
     * @param value the value that should be rounded
     * @return the rounded double
     */
    private static double round(double value, int places) {

        // toString is very important to prevent issues with representing inexact values
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
