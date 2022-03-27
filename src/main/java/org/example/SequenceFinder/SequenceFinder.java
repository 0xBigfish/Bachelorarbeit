package org.example.SequenceFinder;

import org.example.SequenceFinder.Model.GeometricObjects.Package;
import org.example.SequenceFinder.Model.GeometricObjects.Point;
import org.example.SequenceFinder.Model.Octree.LooseOctree;

/**
 * The main class of the SequenceFinder package.
 */
public class SequenceFinder {

    public static void main(String[] args) {
        // L-shape
        Package package1 = new Package(new Point(0, 0, 0), new Point(1, 1, 1), 0, 0);
        Package package2 = new Package(new Point(0, 1, 0), new Point(1, 2, 1), 1, 0);
        Package package3 = new Package(new Point(1, 0, 0), new Point(2, 1, 1), 2, 0);

        LooseOctree<Package> looseOctree = new LooseOctree<>(3, 4);
        looseOctree.insertObject(package1);
        looseOctree.insertObject(package2);
        looseOctree.insertObject(package3);

        System.out.println(looseOctree);
    }
}
