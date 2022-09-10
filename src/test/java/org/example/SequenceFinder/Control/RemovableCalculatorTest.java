package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.GeometricObjects.Point;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.Model.Graph.GraphNode;
import org.example.SequenceFinder.Model.Octree.LooseOctree;
import org.example.SequenceFinder.OperatingDirection;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.example.SequenceFinder.OperatingDirection.FRONT;
import static org.example.SequenceFinder.OperatingDirection.TOP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemovableCalculatorTest {

    LooseOctree<ConcreteAABB> looseOctree;

    /**
     * Get the content of the nodes and return it as a list
     *
     * @param nodes the nodes of a graph
     * @param <T>   the type of the content the nodes represent
     * @return the content of the nodes as a list
     */
    private <T> ArrayList<T> getNodeContent(Collection<GraphNode<T>> nodes) {
        return nodes.stream()
                .map(GraphNode::getContent)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    /**
     * A concrete box implementation for testing purposes
     */
    static class ConcreteAABB extends AABB {
        public ConcreteAABB(Point vertA, Point vertB) {
            super(vertA, vertB);
        }
    }


    @Nested
    @DisplayName("given a loose octree with 2 boxes")
    class TwoBoxes {

        ConcreteAABB boxA;
        ConcreteAABB boxB;

        ArrayList<ConcreteAABB> removableBoxes = new ArrayList<>();

        Graph<ConcreteAABB> graph;

        @BeforeEach
        void setup() {
            boxA = new ConcreteAABB(new Point(0, 0, 0), new Point(1, 1, 1));
            boxB = new ConcreteAABB(new Point(2, 0, 0), new Point(3, 1, 1));

            looseOctree = new LooseOctree<>(3, 8);
        }

        @Nested
        @DisplayName("given a RemovableCalculator with no OperatingDirection")
        class NoDirectionCalc {
            @Test
            @DisplayName("then an IllegalArgumentExceptions should be thrown")
            void illegalArg() {
                ArrayList<OperatingDirection> opDirs = new ArrayList<>();
                assertThrows(IllegalArgumentException.class, () -> new RemovableCalculator<>(looseOctree, opDirs));
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirections TOP and FRONT")
        class TopFrontCalc {

            RemovableCalculator<ConcreteAABB> topFrontCalculator;

            @BeforeEach
            void setup() {
                ArrayList<OperatingDirection> opDirs = new ArrayList<>();
                opDirs.add(TOP);
                opDirs.add(FRONT);

                topFrontCalculator = new RemovableCalculator<>(looseOctree, opDirs);
            }

            @Nested
            @DisplayName("given the OperatingDirections can not alternate")
            class AlternateFalse {

                @BeforeEach
                void setup() {
                    graph = topFrontCalculator.createMergedGraph(false);
                }

                @Test
                @DisplayName("then both boxes should be removable")
                void removable() {
                    removableBoxes.add(boxA);
                    removableBoxes.add(boxB);


                    assertEquals(removableBoxes, getNodeContent(graph.getCopyOfRemovableNodes()));
                }
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirections FRONT and TOP")
        class FrontTopCalc {

        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection FRONT")
        class FrontCalc {

        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection LEFT and RIGHT")
        class LeftRightCalc {

        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection LEFT and BACK")
        class LeftBackCalc {

        }
    }


    @Nested
    @DisplayName("given a loose octree with 8 boxes")
    class EightBoxes {

        @BeforeEach
        void setup() {
            // Create boxes:
            ConcreteAABB boxA = new ConcreteAABB(new Point(0, 0, 0), new Point(4, 16, 4));
            ConcreteAABB boxB = new ConcreteAABB(new Point(4, 0, 0), new Point(8, 16, 4));
            ConcreteAABB boxC = new ConcreteAABB(new Point(8, 0, 0), new Point(16, 12, 3));
            ConcreteAABB boxD = new ConcreteAABB(new Point(8, 0, 3), new Point(16, 12, 11));
            ConcreteAABB boxE = new ConcreteAABB(new Point(0, 0, 4), new Point(8, 8, 8));
            ConcreteAABB boxF = new ConcreteAABB(new Point(0, 0, 8), new Point(8, 8, 14));
            ConcreteAABB boxG = new ConcreteAABB(new Point(8, 0, 11), new Point(16, 8, 16));
            ConcreteAABB boxH = new ConcreteAABB(new Point(4, 8, 4), new Point(10, 16, 12));

            // Create octree and insert boxes:
            looseOctree = new LooseOctree<>(3, 64);
            looseOctree.insertObject(boxA);
            looseOctree.insertObject(boxB);
            looseOctree.insertObject(boxC);
            looseOctree.insertObject(boxD);
            looseOctree.insertObject(boxE);
            looseOctree.insertObject(boxF);
            looseOctree.insertObject(boxG);
            looseOctree.insertObject(boxH);
        }


        @Nested
        @DisplayName("given a RemovableCalculator with no OperatingDirection")
        class NoDirectionCalc {
            @Test
            @DisplayName("then an IllegalArgumentExceptions should be thrown")
            void illegalArg() {
                ArrayList<OperatingDirection> opDirs = new ArrayList<>();
                assertThrows(IllegalArgumentException.class, () -> new RemovableCalculator<>(looseOctree, opDirs));
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirections TOP and FRONT")
        class TopFrontCalc {

            RemovableCalculator<ConcreteAABB> topFrontCalculator;

            @BeforeEach
            void setup() {

                ArrayList<OperatingDirection> opDirs = new ArrayList<>();
                opDirs.add(TOP);
                opDirs.add(FRONT);

                topFrontCalculator = new RemovableCalculator<>(looseOctree, opDirs);
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirections FRONT and TOP")
        class FrontTopCalc {

        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection FRONT")
        class FrontCalc {

        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection LEFT and RIGHT")
        class LeftRightCalc {

        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection LEFT and BACK")
        class LeftBackCalc {

        }
    }
}