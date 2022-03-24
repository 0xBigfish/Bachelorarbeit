package org.example.SequenceFinder.GeometricObjects;

/**
 * A box-shaped object described by two vertices.
 */
public abstract class Box {

    Point vertexA;
    Point vertexB;

    public Box(Point vertA, Point vertB) {
        this.vertexA = vertA;
        this.vertexB = vertB;
    }

    /**
     * Calculate the center of this Box
     *
     * @return the center of the box
     */
    public Point calcCenter() {
        double x = (vertexA.x + vertexB.x) / 2;
        double y = (vertexA.y + vertexB.y) / 2;
        double z = (vertexA.z + vertexB.z) / 2;

        return new Point(x, y, z);
    }

    /**
     * Radius of the box. <br>
     * <b>Import note:</b> the radius does not fully enclose the box. It only touches the center of the box's borders
     * @return the radius of the box
     */
    public double calcRadius() {
        Point center = calcCenter();

        // doesn't matter whether to use vertexA oder vertexB, as the center point has equal distance
        // to both points, and therefore to sides of the
        return Math.max(
                Math.max(
                        Math.abs(center.x - vertexA.x),
                        Math.abs(center.y - vertexA.y)
                ),
                Math.abs(center.z - vertexA.z)
        );
    }
}
