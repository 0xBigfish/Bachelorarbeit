package org.example.SequenceFinder.Model.GeometricObjects;

/**
 * A package in the stack that will be sequenced.
 */
public class Package extends AABB {

    private int id;
    private int articleNo;

    public Package(Point vertA, Point vertB, int id, int articleNo) {
        super(vertA, vertB);
        this.id = id;
        this.articleNo = articleNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(int articleNo) {
        this.articleNo = articleNo;
    }
}
