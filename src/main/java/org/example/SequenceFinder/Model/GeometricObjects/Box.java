package org.example.SequenceFinder.Model.GeometricObjects;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.example.SequenceFinder.OperatingDirection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import static org.example.SequenceFinder.Model.GeometricObjects.Box.BoxVertex.*;

//TODO: rename to AABBox (axis aligned bounding box) and write test to ensure it is axis aligned


/**
 * A box-shaped object described by two vertices.
 */
public abstract class Box {

    /**
     * Enum for the vertices of the box
     */
    public enum BoxVertex {
        FRONT_BOTTOM_LEFT,
        FRONT_BOTTOM_RIGHT,
        FRONT_TOP_LEFT,
        FRONT_TOP_RIGHT,
        BACK_BOTTOM_LEFT,
        BACK_BOTTOM_RIGHT,
        BACK_TOP_LEFT,
        BACK_TOP_RIGHT
    }


    Point vertexA;
    Point vertexB;
    /**
     * rounding to 5 decimals should be sufficient for most cases
     */
    int decimalPlaces = 5;

    public Box(Point vertA, Point vertB) {
        this.vertexA = vertA;
        this.vertexB = vertB;

        if (vertA.x >= vertB.x ||
                vertA.y >= vertB.y ||
                vertA.z >= vertB.z) {
            throw new IllegalArgumentException("Vertex A (bottom left corner) must be smaller than vertex B (upper " +
                    "right corner) \n" +
                    "Vertex A: " + vertA + "\n" +
                    "Vertex B: " + vertB);
        }
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
    public HashMap<BoxVertex, Point> getVertices() {
        Point frontLowerLeft = vertexA;
        Point frontLowerRight = new Point(vertexB.x, vertexA.y, vertexA.z);
        Point frontUpperLeft = new Point(vertexA.x, vertexA.y, vertexB.z);
        Point frontUpperRight = new Point(vertexB.x, vertexA.y, vertexB.z);
        Point backLowerLeft = new Point(vertexA.x, vertexB.y, vertexA.z);
        Point backLowerRight = new Point(vertexB.x, vertexB.y, vertexA.z);
        Point backUpperLeft = new Point(vertexA.x, vertexB.y, vertexB.z);
        Point backUpperRight = vertexB;

        HashMap<BoxVertex, Point> vertices = new HashMap<>();
        vertices.put(FRONT_BOTTOM_LEFT, frontLowerLeft);
        vertices.put(FRONT_BOTTOM_RIGHT, frontLowerRight);
        vertices.put(FRONT_TOP_LEFT, frontUpperLeft);
        vertices.put(FRONT_TOP_RIGHT, frontUpperRight);
        vertices.put(BACK_BOTTOM_LEFT, backLowerLeft);
        vertices.put(BACK_BOTTOM_RIGHT, backLowerRight);
        vertices.put(BACK_TOP_LEFT, backUpperLeft);
        vertices.put(BACK_TOP_RIGHT, backUpperRight);

        return vertices;
    }

    /**
     * Get the plane of the box whose normal vector is pointing towards the given operating direction
     *
     * @param opDir the operating direction
     * @return the plane of the box whose normal vector is pointing towards the given operating direction
     */
    public Plane getSide(OperatingDirection opDir) {
        // tolerance below which points on the plane are considered identical
        double tolerance = 1e-10;

        HashMap<BoxVertex, Point> vertices = getVertices();
        switch (opDir) {
            case FRONT:
                return new Plane(vertices.get(FRONT_TOP_RIGHT).toVector3D(), new Vector3D(0, -1, 0), tolerance);

            case BACK:
                return new Plane(vertices.get(BACK_TOP_RIGHT).toVector3D(), new Vector3D(0, 1, 0), tolerance);

            case LEFT:
                return new Plane(vertices.get(BACK_TOP_LEFT).toVector3D(), new Vector3D(-1, 0, 0), tolerance);

            case RIGHT:
                return new Plane(vertices.get(BACK_TOP_RIGHT).toVector3D(), new Vector3D(1, 0, 0), tolerance);

            case TOP:
                return new Plane(vertices.get(FRONT_TOP_LEFT).toVector3D(), new Vector3D(0, 0, 1), tolerance);

            default:
                throw new RuntimeException("Unknown operating direction: " + opDir);
        }
    }

    @Override
    public String toString() {
        return "Box{" +
                "vertexA=" + vertexA + ", " +
                "vertexB=" + vertexB +
                '}';
    }

    /**
     * Round values to prevent float point precision errors. <br> Example: (-2.3 + 1.0) = -0.65 but the calculated
     * result is -0.6499999999999999 <br>
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
