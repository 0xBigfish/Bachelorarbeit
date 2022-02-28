package org.example.SequenceFinder;

import org.example.SequenceFinder.GeometricObjects.Package;
import org.example.SequenceFinder.GeometricObjects.Point;
import org.example.SequenceFinder.Octree.Octree;

/**
 * The main class of the SequenceFinder package.
 */
public class SequenceFinder {

    public static void main(String[] args) {
        // L-shape
        Package package1 = new Package(new Point(0, 0, 0), new Point(1, 1, 1), 0, 0);
        Package package2 = new Package(new Point(0, 1, 0), new Point(1, 2, 1), 1, 0);
        Package package3 = new Package(new Point(1, 0, 0), new Point(2, 1, 1), 2, 0);

        Octree<Package> octree = new Octree<>(3, 4);
        octree.insertObject(package1);
        octree.insertObject(package2);
        octree.insertObject(package3);

        System.out.println(octree);
    }
}
