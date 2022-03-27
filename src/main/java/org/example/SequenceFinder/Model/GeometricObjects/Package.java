package org.example.SequenceFinder.Model.GeometricObjects;

/**
 * A package in the stack that will be sequenced.
 */
public class Package extends Box {

    private int id;
    private int articleNo;

    public Package(Point vertA, Point vertB, int id, int articleNo) {
        super(vertA, vertB);
        this.id = id;
        this.articleNo = articleNo;
    }
}
