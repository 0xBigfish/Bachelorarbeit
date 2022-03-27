package org.example.SequenceFinder.Model.GeometricObjects;

/**
 * A single Point in a 3D space.
 */
public class Point {

    public double x;
    public double y;
    public double z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point otherPoint = (Point) o;
            return this.x == otherPoint.x &&
                    this.y == otherPoint.y &&
                    this.z == otherPoint.z;

        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
