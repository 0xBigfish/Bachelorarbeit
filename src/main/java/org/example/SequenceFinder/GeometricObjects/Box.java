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
}
