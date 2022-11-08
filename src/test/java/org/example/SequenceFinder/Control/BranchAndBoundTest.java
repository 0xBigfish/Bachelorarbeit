package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Control.Cost.CostFunction;
import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.GeometricObjects.Package;
import org.example.SequenceFinder.Model.GeometricObjects.Point;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.OperatingDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class BranchAndBoundTest {

    Collection<CostFunction<TestPackage>> costFunctions;


    /**
     * A test specific implementation of a package in order to not be impacted by changes to {@linkplain Package}.
     */
    private static class TestPackage extends AABB {
        String articleNumber;

        TestPackage(Point vertA, Point vertB, String artNo) {
            super(vertA, vertB);
            this.articleNumber = artNo;
        }
    }


    /**
     * A test specific implementation of the cost function in order to not be impacted by changes to
     * {@linkplain org.example.SequenceFinder.Control.Cost.HeightDifference}
     */
    private static class TestHeightDifference implements CostFunction<TestPackage> {

        private final double costPerUnit;

        public TestHeightDifference(double costPerUnit) {
            this.costPerUnit = costPerUnit;
        }

        @Override
        public double calcCost(TestPackage nodeA, TestPackage nodeB) {
            return costPerUnit * Math.abs(nodeA.calcCenter().z - nodeB.calcCenter().z);
        }

        @Override
        public double lowerBound() {
            return 0;
        }
    }


    /**
     * A test specific implementation of the cost function
     * {@linkplain org.example.SequenceFinder.Control.Cost.ItemChange}
     */
    private static class TestItemChange implements CostFunction<TestPackage> {

        private final double costPerChange;

        public TestItemChange(double costPerChange) {
            this.costPerChange = costPerChange;
        }

        @Override
        public double calcCost(TestPackage nodeA, TestPackage nodeB) {
            return costPerChange;
        }

        @Override
        public double lowerBound() {
            return 0;
        }
    }


    @Nested
    @DisplayName("given a Stack specified in src/test/resources/branchAndBoundStack.html")
    class Stack {
        //TODO: add file
        Map<OperatingDirection, Graph<TestPackage>> graphsMap = new HashMap<>();


        @Nested
        @DisplayName("given OperatingDirections FRONT and LEFT")
        class OpDirsFRONTAndLEFT {
            @BeforeEach
            void setup() {
                Set<OperatingDirection> frontSet = new HashSet<>();
                frontSet.add(OperatingDirection.FRONT);
                Graph<TestPackage> frontGraph = new Graph<>(frontSet);

                Set<OperatingDirection> leftSet = new HashSet<>();
                leftSet.add(OperatingDirection.LEFT);
                Graph<TestPackage> leftGraph = new Graph<TestPackage>(leftSet);

                // setup nodes
                //TODO: find a way to extract coordinates from software to this extension
//                TestPackage green01 = new TestPackage()
            }
        }

        @Nested
        @DisplayName("given cost 50 for an item change")
        class ItemChange {

            @BeforeEach
            void setup() {
                costFunctions.add(new TestItemChange(50));
            }

            @Nested
            @DisplayName("given cost 2 per unit height difference between packages")
            class HeightDiff {

                @BeforeEach
                void setup() {
                    costFunctions.add(new TestHeightDifference(2));
                }
            }
        }
    }
}