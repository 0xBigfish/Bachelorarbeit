package org.example.SequenceFinder.Model.Octree;

/**
 * Describes the view frustum. It is used to identify objects that lay in front or above another object in the octree.
 * <br>
 * <br>
 * A box shaped view frustum is created which looks to the{@link org.example.SequenceFinder.OperatingDirection},
 * any objects that fully or partially lay within an objects view frustum must be removed before the object itself can
 * be removed.
 */
public class Frustum {

    Plane front;
    Plane bottom;
    Plane top;
    Plane back;
    Plane left;
    Plane right;

    public Frustum(Plane front, Plane bottom, Plane top, Plane back, Plane left, Plane right) {
        this.front = front;
        this.bottom = bottom;
        this.top = top;
        this.back = back;
        this.left = left;
        this.right = right;
    }
}
