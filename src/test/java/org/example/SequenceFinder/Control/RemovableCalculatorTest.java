package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.GeometricObjects.Point;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.Model.Graph.GraphNode;
import org.example.SequenceFinder.Model.Octree.LooseOctree;
import org.example.SequenceFinder.OperatingDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.example.SequenceFinder.OperatingDirection.*;
import static org.junit.jupiter.api.Assertions.*;

class RemovableCalculatorTest {

    LooseOctree<AABB> looseOctree;
    HashSet<OperatingDirection> opDirs = new HashSet<>();
    Map<OperatingDirection, Graph<AABB>> graphMap = new HashMap<>();

    /**
     * Asserts that the given number of unique graphs are in the given map.
     *
     * @param i        the expected number of unique graphs
     * @param graphMap the map where the graphs are stored
     * @param <T>      the type of content the graph stores
     */
    private static <T> void assertNumberOfUniqueGraphs(int i, Map<OperatingDirection, Graph<T>> graphMap) {
        // in a set no duplicate elements are allowed
        HashSet<Graph<T>> uniqueGraphs = new HashSet<>(graphMap.values());
        assertEquals(i, uniqueGraphs.size(), "The number of unique graphs is not correct!");
    }

    /**
     * Assert that an object is removable from the graph for the given direction.
     *
     * @param object the object to check
     * @param graph  the graph where the object should be removable
     * @param opDir  the direction from which the object should be removable
     * @param <T>    the type of content the graph stores
     */
    private static <T> void assertRemovableFromFor(T object, Graph<T> graph, OperatingDirection opDir) {
        GraphNode<T> node = graph.getCopyOfRemovableNodes().stream()
                .filter(n -> n.getContent().equals(object))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "The object  is not in the graph's set of removable nodes! \n" +
                                "Object: " + object));

        assertTrue(node.getCopyOfIncomingNodes(opDir).isEmpty(),
                "The node " + node + " is not removable!");
    }

    /**
     * Assert that an object is NOT removable from the graph for the given direction.
     *
     * @param object the object to check
     * @param graph  the graph where the object should be removable
     * @param opDir  the direction from which the object should NOT be removable
     * @param <T>    the type of content the graph stores
     */
    private static <T> void assertNotRemovableFromFor(T object, Graph<T> graph, OperatingDirection opDir) {
        GraphNode<T> node = graph.getCopyOfNodes().stream()
                .filter(n -> n.getContent().equals(object))
                .findFirst()
                .orElseThrow(() -> new AssertionError("The object is not in the graph! \n" +
                        "Object: " + object));

        assertFalse(node.getCopyOfIncomingNodes(opDir).isEmpty(),
                "The node " + node + " is removable!");
    }

    /**
     * Assert that the graphMap contains only graphs for the given operating directions.
     *
     * @param opDirs the OperatingDirections
     */
    private void assertGraphMapContainsOnly(Collection<OperatingDirection> opDirs) {
        assertEquals(opDirs.size(), graphMap.keySet().size(), "The number of graphs in the map is not correct!");

        for (OperatingDirection opDir : opDirs) {
            assertTrue(graphMap.containsKey(opDir),
                    "The map does not contain the graph for the operating direction " + opDir);
        }
    }


    @Nested
    @DisplayName("given a loose octree with 2 boxes")
    class TwoBoxes {

        AABB boxA;
        AABB boxB;

        @BeforeEach
        void setup() {
            boxA = new AABB(new Point(0, 0, 0), new Point(1, 1, 1));
            boxB = new AABB(new Point(2, 0, 0), new Point(3, 1, 1));

            looseOctree = new LooseOctree<>(3, 8);
            looseOctree.insertObject(boxA);
            looseOctree.insertObject(boxB);
        }

        @Nested
        @DisplayName("given a RemovableCalculator with no OperatingDirection")
        class NoDirectionCalc {
            @Test
            @DisplayName("then an IllegalArgumentExceptions should be thrown")
            void illegalArg() {
                assertThrows(IllegalArgumentException.class, () -> new RemovableCalculator<>(looseOctree, opDirs));
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirections TOP and FRONT")
        class TopFrontCalc {

            RemovableCalculator<AABB> topFrontCalculator;

            @BeforeEach
            void setup() {
                opDirs.add(TOP);
                opDirs.add(FRONT);

                topFrontCalculator = new RemovableCalculator<>(looseOctree, opDirs);
            }

            @Nested
            @DisplayName("when the OperatingDirections can not change")
            class ChangeFalse {
                boolean opDirCanChange = false;

                @BeforeEach
                void setup() {
                    graphMap = topFrontCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 2 unique graphs")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(2, graphMap);
                }

                @Test
                @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(TOP);
                    set.add(FRONT);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for TOP should contain 2 nodes")
                void topNodes() {
                    assertEquals(2, graphMap.get(TOP).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for FRONT should contain 2 nodes")
                void frontNodes() {
                    assertEquals(2, graphMap.get(FRONT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then both boxes should be removable in the FRONT graph")
                void removableFront() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                            () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT)
                    );
                }

                @Test
                @DisplayName("then both boxes should be removable in the TOP graph")
                void removableTop() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP)
                    );
                }
            }


            @Nested
            @DisplayName("when the OperatingDirections can change")
            class ChangeTrue {
                boolean opDirCanChange = true;

                @BeforeEach
                void setup() {
                    graphMap = topFrontCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 1 unique graph")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(1, graphMap);
                }

                @Test
                @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(TOP);
                    set.add(FRONT);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for TOP should contain 2 nodes")
                void topNodes() {
                    assertEquals(2, graphMap.get(TOP).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for FRONT should contain 2 nodes")
                void frontNodes() {
                    assertEquals(2, graphMap.get(FRONT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then both boxes should be removable when accessing from the FRONT")
                void removableFront() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                            () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT)
                    );
                }

                @Test
                @DisplayName("then both boxes should be removable when accessing from the TOP")
                void removableTop() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP)
                    );
                }
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection FRONT")
        class FrontCalc {

            RemovableCalculator<AABB> frontCalculator;

            @BeforeEach
            void setup() {
                opDirs.add(FRONT);
                frontCalculator = new RemovableCalculator<>(looseOctree, opDirs);
            }


            @Nested
            @DisplayName("given the OperatingDirection can change")
            class ChangeTrue {
                boolean opDirCanChange = true;


                @BeforeEach
                void setup() {
                    graphMap = frontCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 1 unique graph")
                void numberOfGraphs() {
                    assertEquals(1, graphMap.size());
                }

                @Test
                @DisplayName("then there should only be the FRONT graph in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(FRONT);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for FRONT should contain 2 nodes")
                void frontNodes() {
                    assertEquals(2, graphMap.get(FRONT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then both boxes should be removable when accessing from the FRONT")
                void removableFront() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                            () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT)
                    );
                }
            }


            @Nested
            @DisplayName("given the OperatingDirection can not change")
            class ChangeFalse {
                boolean opDirCanChange = false;


                @BeforeEach
                void setup() {
                    graphMap = frontCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 1 graph")
                void numberOfGraphs() {
                    assertEquals(1, graphMap.size());
                }

                @Test
                @DisplayName("then there should only be the FRONT graph in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(FRONT);
                    assertGraphMapContainsOnly(set);
                }


                @Test
                @DisplayName("then the graph for FRONT should contain 2 nodes")
                void frontNodes() {
                    assertEquals(2, graphMap.get(FRONT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then both boxes should be removable in the FRONT graph")
                void removableFront() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                            () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT)
                    );
                }
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection LEFT and RIGHT")
        class LeftRightCalc {

            RemovableCalculator<AABB> leftRightCalculator;

            @BeforeEach
            void setup() {
                opDirs.add(LEFT);
                opDirs.add(RIGHT);

                leftRightCalculator = new RemovableCalculator<>(looseOctree, opDirs);
            }

            @Nested
            @DisplayName("when the OperatingDirections can not change")
            class ChangeFalse {
                boolean opDirCanChange = false;

                @BeforeEach
                void setup() {
                    graphMap = leftRightCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 2 unique graphs")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(2, graphMap);
                }

                @Test
                @DisplayName("then there should only be the LEFT and RIGHT graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(LEFT);
                    set.add(RIGHT);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for LEFT should contain 2 nodes")
                void topNodes() {
                    assertEquals(2, graphMap.get(LEFT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for RIGHT should contain 2 nodes")
                void frontNodes() {
                    assertEquals(2, graphMap.get(RIGHT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then only BoxA should be removable in the LEFT graph")
                void removableLEFT() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(LEFT), LEFT),
                            () -> assertNotRemovableFromFor(boxB, graphMap.get(LEFT), LEFT)
                    );
                }

                @Test
                @DisplayName("then only BoxB should be removable in the RIGHT graph")
                void removableRIGHT() {
                    assertAll(
                            () -> assertNotRemovableFromFor(boxA, graphMap.get(RIGHT), RIGHT),
                            () -> assertRemovableFromFor(boxB, graphMap.get(RIGHT), RIGHT)
                    );
                }
            }


            @Nested
            @DisplayName("when the OperatingDirections can change")
            class ChangeTrue {
                boolean opDirCanChange = true;

                @BeforeEach
                void setup() {
                    graphMap = leftRightCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 1 unique graph")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(1, graphMap);
                }

                @Test
                @DisplayName("then there should only be the LEFT and RIGHT graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(LEFT);
                    set.add(RIGHT);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for LEFT should contain 2 nodes")
                void topNodes() {
                    assertEquals(2, graphMap.get(LEFT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for RIGHT should contain 2 nodes")
                void frontNodes() {
                    assertEquals(2, graphMap.get(RIGHT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then only BoxA should be removable when accessing from the LEFT")
                void removableFront() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(LEFT), LEFT),
                            () -> assertNotRemovableFromFor(boxB, graphMap.get(RIGHT), LEFT)
                    );
                }

                @Test
                @DisplayName("then only BoxB should be removable when accessing from the RIGHT")
                void removableRIGHT() {
                    assertAll(
                            () -> assertRemovableFromFor(boxB, graphMap.get(RIGHT), RIGHT),
                            () -> assertNotRemovableFromFor(boxA, graphMap.get(RIGHT), RIGHT)
                    );
                }
            }
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirection LEFT and BACK")
        class LeftBackCalc {

            RemovableCalculator<AABB> leftBackCalculator;

            @BeforeEach
            void setup() {
                opDirs.add(LEFT);
                opDirs.add(BACK);

                leftBackCalculator = new RemovableCalculator<>(looseOctree, opDirs);
            }

            @Nested
            @DisplayName("when the OperatingDirections can not change")
            class ChangeFalse {
                boolean opDirCanChange = false;

                @BeforeEach
                void setup() {
                    graphMap = leftBackCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 2 unique graphs")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(2, graphMap);
                }

                @Test
                @DisplayName("then there should only be the LEFT and BACK graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(LEFT);
                    set.add(BACK);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for LEFT should contain 2 nodes")
                void topNodes() {
                    assertEquals(2, graphMap.get(LEFT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for BACK should contain 2 nodes")
                void frontNodes() {
                    assertEquals(2, graphMap.get(BACK).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then only BoxA should be removable in the LEFT graph")
                void removableLEFT() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(LEFT), LEFT),
                            () -> assertNotRemovableFromFor(boxB, graphMap.get(LEFT), LEFT)
                    );
                }

                @Test
                @DisplayName("then both boxes should be removable in the BACK graph")
                void removableRIGHT() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(BACK), BACK),
                            () -> assertRemovableFromFor(boxB, graphMap.get(BACK), BACK)
                    );
                }
            }


            @Nested
            @DisplayName("when the OperatingDirections can change")
            class ChangeTrue {
                boolean opDirCanChange = true;

                @BeforeEach
                void setup() {
                    graphMap = leftBackCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 1 unique graph")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(1, graphMap);
                }

                @Test
                @DisplayName("then there should only be the LEFT and BACK graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(LEFT);
                    set.add(BACK);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for LEFT should contain 2 nodes")
                void leftNodes() {
                    assertEquals(2, graphMap.get(LEFT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for BACK should contain 2 nodes")
                void backNodes() {
                    assertEquals(2, graphMap.get(BACK).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then only BoxA should be removable when accessing from the LEFT")
                void removableLEFT() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(LEFT), LEFT),
                            () -> assertNotRemovableFromFor(boxB, graphMap.get(LEFT), LEFT)
                    );
                }

                @Test
                @DisplayName("then both boxes should be removable when accessing from the BACK")
                void removableBACK() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(BACK), BACK),
                            () -> assertRemovableFromFor(boxB, graphMap.get(BACK), BACK)
                    );
                }
            }
        }
    }


    @Nested
    @DisplayName("given a loose octree with 4 boxes")
    class FourBoxes {
        AABB boxA;
        AABB boxB;
        AABB boxC;
        AABB boxD;

        @BeforeEach
        void setup() {
            // Create boxes:
            // The boxes are arranged in a square with the following coordinates, with a 1 unit gap between each box:
            //
            // (the gap is important, otherwise the boxes overlap and no box is removable because they intersect each
            // other)
            boxA = new AABB(new Point(0, 0, 0), (new Point(0.9, 0.9, 0.9)));
            boxB = new AABB(new Point(1, 0, 0), (new Point(1.9, 1.9, 1.9)));
            boxC = new AABB(new Point(0, 1, 0), (new Point(0.9, 1.9, 0.9)));
            boxD = new AABB(new Point(1, 1, 0), (new Point(1.9, 1.9, 1.9)));

            // Create octree and insert boxes:
            looseOctree = new LooseOctree<>(3, 8);
            looseOctree.insertObject(boxA);
            looseOctree.insertObject(boxB);
            looseOctree.insertObject(boxC);
            looseOctree.insertObject(boxD);
        }


        @Nested
        @DisplayName("given a RemovableCalculator with OperatingDirections TOP and FRONT")
        class TopFrontCalc {

            RemovableCalculator<AABB> topFrontCalculator;

            @BeforeEach
            void setup() {
                opDirs.add(TOP);
                opDirs.add(FRONT);

                topFrontCalculator = new RemovableCalculator<>(looseOctree, opDirs);
            }

            @Nested
            @DisplayName("when the OperatingDirections can not change")
            class ChangeFalse {
                boolean opDirCanChange = false;

                @BeforeEach
                void setup() {
                    graphMap = topFrontCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 2 unique graphs")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(2, graphMap);
                }

                @Test
                @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(TOP);
                    set.add(FRONT);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for TOP should contain 4 nodes")
                void topNodes() {
                    assertEquals(4, graphMap.get(TOP).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for FRONT should contain 4 nodes")
                void frontNodes() {
                    assertEquals(4, graphMap.get(FRONT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then only boxA and boxB should be removable in the FRONT graph")
                void removableFront() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                            () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                            () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT),
                            () -> assertNotRemovableFromFor(boxD, graphMap.get(FRONT), FRONT)
                    );
                }

                @Test
                @DisplayName("then all four boxes should be removable in the TOP graph")
                void removableTop() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                    );
                }

                @Nested
                @DisplayName("when FRONT is chosen as the OperatingDirection")
                class OpDirFRONT {
                    OperatingDirection chosenOpDir = FRONT;


                    @Nested
                    @DisplayName("when boxB is removed")
                    class BoxBRemoved {

                        GraphNode<AABB> nodeB;

                        @BeforeEach
                        void setup() {
                            nodeB = graphMap.get(chosenOpDir).getCopyOfNodes().stream()
                                    .filter(node -> node.getContent().equals(boxB))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("Node for boxB not found"));

                            graphMap.get(chosenOpDir).removeNode(nodeB);
                        }

                        @Test
                        @DisplayName("then the there should be 2 unique graphs")
                        void numberOfGraphs() {
                            assertNumberOfUniqueGraphs(2, graphMap);
                        }

                        @Test
                        @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                        void graphMap() {
                            Set<OperatingDirection> set = new HashSet<>();
                            set.add(TOP);
                            set.add(FRONT);
                            assertGraphMapContainsOnly(set);
                        }

                        @Test
                        @DisplayName("then the graph for TOP should contain 4 nodes")
                        void topNodes() {
                            assertEquals(4, graphMap.get(TOP).getCopyOfNodes().size());
                        }

                        @Test
                        @DisplayName("then the graph for FRONT should contain 3 nodes")
                        void frontNodes() {
                            assertEquals(3, graphMap.get(FRONT).getCopyOfNodes().size());
                        }


                        @Test
                        @DisplayName("then only boxA and boxD should be removable in the FRONT graph")
                        void removableFront() {
                            assertAll(
                                    () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                                    () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT),
                                    () -> assertRemovableFromFor(boxD, graphMap.get(FRONT), FRONT)
                            );
                        }

                        @Test
                        @DisplayName("then all four boxes should be removable in the TOP graph")
                        void removableTop() {
                            assertAll(
                                    () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                                    () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                                    () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP),
                                    () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                            );
                        }

                        @Nested
                        @DisplayName("when boxD is removed")
                        class BoxDRemoved {
                            GraphNode<AABB> nodeD;

                            @BeforeEach
                            void setup() {
                                nodeD = graphMap.get(chosenOpDir).getCopyOfNodes().stream()
                                        .filter(node -> node.getContent().equals(boxD))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("Node for boxD not found"));

                                graphMap.get(chosenOpDir).removeNode(nodeD);
                            }

                            @Test
                            @DisplayName("then the there should be 2 unique graphs")
                            void numberOfGraphs() {
                                assertNumberOfUniqueGraphs(2, graphMap);
                            }

                            @Test
                            @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                            void graphMap() {
                                Set<OperatingDirection> set = new HashSet<>();
                                set.add(TOP);
                                set.add(FRONT);
                                assertGraphMapContainsOnly(set);
                            }

                            @Test
                            @DisplayName("then the graph for TOP should contain 4 nodes")
                            void topNodes() {
                                assertEquals(4, graphMap.get(TOP).getCopyOfNodes().size());
                            }

                            @Test
                            @DisplayName("then the graph for FRONT should contain 2 nodes")
                            void frontNodes() {
                                assertEquals(2, graphMap.get(FRONT).getCopyOfNodes().size());
                            }


                            @Test
                            @DisplayName("then only boxA should be removable in the FRONT graph")
                            void removableFront() {
                                assertAll(
                                        () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                                        () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT)
                                );
                            }

                            @Test
                            @DisplayName("then all four boxes should be removable in the TOP graph")
                            void removableTop() {
                                assertAll(
                                        () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                                        () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                                        () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP),
                                        () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                                );
                            }

                            @Nested
                            @DisplayName("when boxA is removed")
                            class BoxARemoved {
                                GraphNode<AABB> nodeA;

                                @BeforeEach
                                void setup() {
                                    nodeA = graphMap.get(chosenOpDir).getCopyOfNodes().stream()
                                            .filter(node -> node.getContent().equals(boxA))
                                            .findFirst()
                                            .orElseThrow(() -> new IllegalStateException("Node for boxA not found"));

                                    graphMap.get(chosenOpDir).removeNode(nodeA);
                                }

                                @Test
                                @DisplayName("then the there should be 2 unique graphs")
                                void numberOfGraphs() {
                                    assertNumberOfUniqueGraphs(2, graphMap);
                                }

                                @Test
                                @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                                void graphMap() {
                                    Set<OperatingDirection> set = new HashSet<>();
                                    set.add(TOP);
                                    set.add(FRONT);
                                    assertGraphMapContainsOnly(set);
                                }

                                @Test
                                @DisplayName("then the graph for TOP should contain 4 nodes")
                                void topNodes() {
                                    assertEquals(4, graphMap.get(TOP).getCopyOfNodes().size());
                                }

                                @Test
                                @DisplayName("then the graph for FRONT should contain 1 nodes")
                                void frontNodes() {
                                    assertEquals(1, graphMap.get(FRONT).getCopyOfNodes().size());
                                }

                                @Test
                                @DisplayName("then only the remaining boxC should be removable in the FRONT graph")
                                void removableFront() {
                                    assertAll(
                                            () -> assertRemovableFromFor(boxC, graphMap.get(FRONT), FRONT)
                                    );
                                }

                                @Test
                                @DisplayName("then all four boxes should be removable in the TOP graph")
                                void removableTop() {
                                    assertAll(
                                            () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                                            () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                                            () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP),
                                            () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                                    );
                                }

                                @Nested
                                @DisplayName("when boxC is removed")
                                class BoxCRemoved {
                                    GraphNode<AABB> nodeC;

                                    @BeforeEach
                                    void setup() {
                                        nodeC = graphMap.get(chosenOpDir).getCopyOfNodes().stream()
                                                .filter(node -> node.getContent().equals(boxC))
                                                .findFirst()
                                                .orElseThrow(() -> new IllegalStateException("Node for boxC not " +
                                                        "found"));

                                        graphMap.get(chosenOpDir).removeNode(nodeC);
                                    }

                                    @Test
                                    @DisplayName("then the there should be 2 unique graphs")
                                    void numberOfGraphs() {
                                        assertNumberOfUniqueGraphs(2, graphMap);
                                    }

                                    @Test
                                    @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                                    void graphMap() {
                                        Set<OperatingDirection> set = new HashSet<>();
                                        set.add(TOP);
                                        set.add(FRONT);
                                        assertGraphMapContainsOnly(set);
                                    }

                                    @Test
                                    @DisplayName("then the graph for TOP should contain 4 nodes")
                                    void topNodes() {
                                        assertEquals(4, graphMap.get(TOP).getCopyOfNodes().size());
                                    }

                                    @Test
                                    @DisplayName("then the graph for FRONT should contain 0 nodes")
                                    void frontNodes() {
                                        assertEquals(0, graphMap.get(FRONT).getCopyOfNodes().size());
                                    }

                                    @Test
                                    @DisplayName("then no box should be removable in the FRONT graph")
                                    void removableFront() {
                                        assertTrue(graphMap.get(FRONT).getCopyOfNodes().isEmpty());
                                    }

                                    @Test
                                    @DisplayName("then all four boxes should be removable in the TOP graph")
                                    void removableTop() {
                                        assertAll(
                                                () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                                                () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                                                () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP),
                                                () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                                        );
                                    }
                                }
                            }
                        }
                    }
                }


                @Nested
                @DisplayName("when TOP is chosen as the OperatingDirection to remove from")
                class OpDirTOP {
                    OperatingDirection chosenOpDir = TOP;


                    @Nested
                    @DisplayName("when boxC is removed")
                    class BoxCRemoved {

                        GraphNode<AABB> nodeC;

                        @BeforeEach
                        void setup() {
                            nodeC = graphMap.get(chosenOpDir).getCopyOfNodes().stream()
                                    .filter(node -> node.getContent().equals(boxC))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("Node for boxC not found"));

                            graphMap.get(chosenOpDir).removeNode(nodeC);
                        }

                        @Test
                        @DisplayName("then the there should be 2 unique graphs")
                        void numberOfGraphs() {
                            assertNumberOfUniqueGraphs(2, graphMap);
                        }

                        @Test
                        @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                        void graphMap() {
                            Set<OperatingDirection> set = new HashSet<>();
                            set.add(TOP);
                            set.add(FRONT);
                            assertGraphMapContainsOnly(set);
                        }

                        @Test
                        @DisplayName("then the graph for TOP should contain 3 nodes")
                        void topNodes() {
                            assertEquals(3, graphMap.get(TOP).getCopyOfNodes().size());
                        }

                        @Test
                        @DisplayName("then the graph for FRONT should contain 4 nodes")
                        void frontNodes() {
                            assertEquals(4, graphMap.get(FRONT).getCopyOfNodes().size());
                        }


                        @Test
                        @DisplayName("then only boxA and boxB should be removable in the FRONT graph")
                        void removableFront() {
                            assertAll(
                                    () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                                    () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                                    () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT),
                                    () -> assertNotRemovableFromFor(boxD, graphMap.get(FRONT), FRONT)
                            );
                        }

                        @Test
                        @DisplayName("then all three remaining boxes should be removable in the TOP graph")
                        void removableTop() {
                            assertAll(
                                    () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                                    () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                                    () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                            );
                        }

                        @Nested
                        @DisplayName("when boxB is removed")
                        class BoxBRemoved {
                            GraphNode<AABB> nodeB;

                            @BeforeEach
                            void setup() {
                                nodeB = graphMap.get(TOP).getCopyOfNodes().stream()
                                        .filter(node -> node.getContent().equals(boxB))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("Node for boxB not found"));

                                graphMap.get(chosenOpDir).removeNode(nodeB);
                            }

                            @Test
                            @DisplayName("then the there should be 2 unique graphs")
                            void numberOfGraphs() {
                                assertNumberOfUniqueGraphs(2, graphMap);
                            }

                            @Test
                            @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                            void graphMap() {
                                Set<OperatingDirection> set = new HashSet<>();
                                set.add(TOP);
                                set.add(FRONT);
                                assertGraphMapContainsOnly(set);
                            }

                            @Test
                            @DisplayName("then the graph for TOP should contain 2 nodes")
                            void topNodes() {
                                assertEquals(2, graphMap.get(TOP).getCopyOfNodes().size());
                            }

                            @Test
                            @DisplayName("then the graph for FRONT should contain 4 nodes")
                            void frontNodes() {
                                assertEquals(4, graphMap.get(FRONT).getCopyOfNodes().size());
                            }


                            @Test
                            @DisplayName("then only boxA and boxB should be removable in the FRONT graph")
                            void removableFront() {
                                assertAll(
                                        () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                                        () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                                        () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT),
                                        () -> assertNotRemovableFromFor(boxD, graphMap.get(FRONT), FRONT)
                                );
                            }

                            @Test
                            @DisplayName("then all two remaining boxes should be removable in the TOP graph")
                            void removableTop() {
                                assertAll(
                                        () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                                        () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                                );
                            }

                            @Nested
                            @DisplayName("when boxA is removed")
                            class BoxARemoved {
                                GraphNode<AABB> nodeA;

                                @BeforeEach
                                void setup() {
                                    nodeA = graphMap.get(TOP).getCopyOfNodes().stream()
                                            .filter(node -> node.getContent().equals(boxA))
                                            .findFirst()
                                            .orElseThrow(() -> new IllegalStateException("Node for boxA not found"));

                                    graphMap.get(chosenOpDir).removeNode(nodeA);
                                }

                                @Test
                                @DisplayName("then the there should be 2 unique graphs")
                                void numberOfGraphs() {
                                    assertNumberOfUniqueGraphs(2, graphMap);
                                }

                                @Test
                                @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                                void graphMap() {
                                    Set<OperatingDirection> set = new HashSet<>();
                                    set.add(TOP);
                                    set.add(FRONT);
                                    assertGraphMapContainsOnly(set);
                                }

                                @Test
                                @DisplayName("then the graph for TOP should contain 1 nodes")
                                void topNodes() {
                                    assertEquals(1, graphMap.get(TOP).getCopyOfNodes().size());
                                }

                                @Test
                                @DisplayName("then the graph for FRONT should contain 4 nodes")
                                void frontNodes() {
                                    assertEquals(4, graphMap.get(FRONT).getCopyOfNodes().size());
                                }


                                @Test
                                @DisplayName("then only boxA and boxB should be removable in the FRONT graph")
                                void removableFront() {
                                    assertAll(
                                            () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                                            () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                                            () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT),
                                            () -> assertNotRemovableFromFor(boxD, graphMap.get(FRONT), FRONT)
                                    );
                                }

                                @Test
                                @DisplayName("then the remaining box boxD should be removable in the TOP graph")
                                void removableTop() {
                                    assertAll(
                                            () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                                    );
                                }

                                @Nested
                                @DisplayName("when boxD is removed")
                                class BoxDRemoved {
                                    GraphNode<AABB> nodeD;

                                    @BeforeEach
                                    void setup() {
                                        nodeD = graphMap.get(chosenOpDir).getCopyOfNodes().stream()
                                                .filter(node -> node.getContent().equals(boxD))
                                                .findFirst()
                                                .orElseThrow(() -> new IllegalStateException("Node for boxD not " +
                                                        "found"));

                                        graphMap.get(chosenOpDir).removeNode(nodeD);
                                    }

                                    @Test
                                    @DisplayName("then the there should be 2 unique graphs")
                                    void numberOfGraphs() {
                                        assertNumberOfUniqueGraphs(2, graphMap);
                                    }

                                    @Test
                                    @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                                    void graphMap() {
                                        Set<OperatingDirection> set = new HashSet<>();
                                        set.add(TOP);
                                        set.add(FRONT);
                                        assertGraphMapContainsOnly(set);
                                    }

                                    @Test
                                    @DisplayName("then the graph for TOP should contain 0 nodes")
                                    void topNodes() {
                                        assertEquals(0, graphMap.get(TOP).getCopyOfNodes().size());
                                    }

                                    @Test
                                    @DisplayName("then the graph for FRONT should contain 4 nodes")
                                    void frontNodes() {
                                        assertEquals(4, graphMap.get(FRONT).getCopyOfNodes().size());
                                    }

                                    @Test
                                    @DisplayName("then boxA and boxB box should be removable in the FRONT graph")
                                    void removableFront() {
                                        assertAll(
                                                () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                                                () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                                                () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT),
                                                () -> assertNotRemovableFromFor(boxD, graphMap.get(FRONT), FRONT)
                                        );
                                    }

                                    @Test
                                    @DisplayName("then no box should be removable in the TOP graph")
                                    void removableTop() {
                                        assertTrue(graphMap.get(TOP).getCopyOfNodes().isEmpty());
                                    }
                                }
                            }
                        }
                    }
                }
            }


            @Nested
            @DisplayName("when the OperatingDirections can change")
            class ChangeTrue {
                boolean opDirCanChange = true;

                @BeforeEach
                void setup() {
                    graphMap = topFrontCalculator.createGraphs(opDirCanChange);
                }

                @Test
                @DisplayName("then the there should be 1 unique graph")
                void numberOfGraphs() {
                    assertNumberOfUniqueGraphs(1, graphMap);
                }

                @Test
                @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                void graphMap() {
                    Set<OperatingDirection> set = new HashSet<>();
                    set.add(TOP);
                    set.add(FRONT);
                    assertGraphMapContainsOnly(set);
                }

                @Test
                @DisplayName("then the graph for TOP should contain 4 nodes")
                void topNodes() {
                    assertEquals(4, graphMap.get(TOP).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then the graph for FRONT should contain 4 nodes")
                void frontNodes() {
                    assertEquals(4, graphMap.get(FRONT).getCopyOfNodes().size());
                }

                @Test
                @DisplayName("then only boxA and boxB should be removable when accessing from the FRONT")
                void removableFront() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                            () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                            () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT),
                            () -> assertNotRemovableFromFor(boxD, graphMap.get(FRONT), FRONT)
                    );
                }

                @Test
                @DisplayName("then all boxes should be removable when accessing from the TOP")
                void removableTOP() {
                    assertAll(
                            () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP),
                            () -> assertRemovableFromFor(boxD, graphMap.get(TOP), TOP)
                    );
                }

                @Nested
                @DisplayName("when boxD is removed")
                class BoxDRemoved {

                    GraphNode<AABB> nodeD;

                    @BeforeEach
                    void setup() {
                        nodeD = graphMap.get(TOP).getCopyOfNodes().stream()
                                .filter(node -> node.getContent().equals(boxD))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Node for boxD not found"));

                        graphMap.get(TOP).removeNode(nodeD);
                    }

                    @Test
                    @DisplayName("then the there should be 1 unique graph")
                    void numberOfGraphs() {
                        assertNumberOfUniqueGraphs(1, graphMap);
                    }

                    @Test
                    @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                    void graphMap() {
                        Set<OperatingDirection> set = new HashSet<>();
                        set.add(TOP);
                        set.add(FRONT);
                        assertGraphMapContainsOnly(set);
                    }

                    @Test
                    @DisplayName("then the graph for TOP should contain 3 nodes")
                    void topNodes() {
                        assertEquals(3, graphMap.get(TOP).getCopyOfNodes().size());
                    }

                    @Test
                    @DisplayName("then the graph for FRONT should contain 3 nodes")
                    void frontNodes() {
                        assertEquals(3, graphMap.get(FRONT).getCopyOfNodes().size());
                    }

                    @Test
                    @DisplayName("then only boxA and boxB should be removable when accessing from the FRONT")
                    void removableFront() {
                        assertAll(
                                () -> assertRemovableFromFor(boxA, graphMap.get(FRONT), FRONT),
                                () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                                () -> assertNotRemovableFromFor(boxC, graphMap.get(FRONT), FRONT)
                        );
                    }

                    @Test
                    @DisplayName("then all three remaining boxes should be removable when accessing from the TOP")
                    void removableTOP() {
                        assertAll(
                                () -> assertRemovableFromFor(boxA, graphMap.get(TOP), TOP),
                                () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                                () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP)
                        );
                    }

                    @Nested
                    @DisplayName("when boxA is removed")
                    class BoxARemoved {
                        GraphNode<AABB> nodeA;

                        @BeforeEach
                        void setup() {
                            nodeA = graphMap.get(TOP).getCopyOfNodes().stream()
                                    .filter(node -> node.getContent().equals(boxA))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("Node for boxA not found"));

                            graphMap.get(TOP).removeNode(nodeA);
                        }


                        @Test
                        @DisplayName("then the there should be 1 unique graph")
                        void numberOfGraphs() {
                            assertNumberOfUniqueGraphs(1, graphMap);
                        }

                        @Test
                        @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                        void graphMap() {
                            Set<OperatingDirection> set = new HashSet<>();
                            set.add(TOP);
                            set.add(FRONT);
                            assertGraphMapContainsOnly(set);
                        }

                        @Test
                        @DisplayName("then the graph for TOP should contain 2 nodes")
                        void topNodes() {
                            assertEquals(2, graphMap.get(TOP).getCopyOfNodes().size());
                        }

                        @Test
                        @DisplayName("then the graph for FRONT should contain 2 nodes")
                        void frontNodes() {
                            assertEquals(2, graphMap.get(FRONT).getCopyOfNodes().size());
                        }

                        @Test
                        @DisplayName("then both remaining boxes should be removable when accessing from the FRONT")
                        void removableFront() {
                            assertAll(
                                    () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT),
                                    () -> assertRemovableFromFor(boxC, graphMap.get(FRONT), FRONT)
                            );
                        }

                        @Test
                        @DisplayName("then both remaining boxes should be removable when accessing from the TOP")
                        void removableTOP() {
                            assertAll(
                                    () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP),
                                    () -> assertRemovableFromFor(boxC, graphMap.get(TOP), TOP)
                            );
                        }

                        @Nested
                        @DisplayName("when boxC is removed")
                        class BoxCRemoved {
                            GraphNode<AABB> nodeC;

                            @BeforeEach
                            void setup() {
                                nodeC = graphMap.get(TOP).getCopyOfNodes().stream()
                                        .filter(node -> node.getContent().equals(boxC))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("Node for boxC not found"));

                                graphMap.get(TOP).removeNode(nodeC);
                            }


                            @Test
                            @DisplayName("then the there should be 1 unique graph")
                            void numberOfGraphs() {
                                assertNumberOfUniqueGraphs(1, graphMap);
                            }

                            @Test
                            @DisplayName("then there should only be the TOP and FRONT graphs in the graphMap")
                            void graphMap() {
                                Set<OperatingDirection> set = new HashSet<>();
                                set.add(TOP);
                                set.add(FRONT);
                                assertGraphMapContainsOnly(set);
                            }

                            @Test
                            @DisplayName("then the graph for TOP should contain 1 nodes")
                            void topNodes() {
                                assertEquals(1, graphMap.get(TOP).getCopyOfNodes().size());
                            }

                            @Test
                            @DisplayName("then the graph for FRONT should contain 1 nodes")
                            void frontNodes() {
                                assertEquals(1, graphMap.get(FRONT).getCopyOfNodes().size());
                            }

                            @Test
                            @DisplayName("then the remaining boxB should be removable when accessing from the FRONT")
                            void removableFront() {
                                assertAll(
                                        () -> assertRemovableFromFor(boxB, graphMap.get(FRONT), FRONT)
                                );
                            }

                            @Test
                            @DisplayName("then the remaining boxB should be removable when accessing from the TOP")
                            void removableTOP() {
                                assertAll(
                                        () -> assertRemovableFromFor(boxB, graphMap.get(TOP), TOP)
                                );
                            }

                            @Nested
                            @DisplayName("when boxB is removed")
                            class BoxBRemoved {
                                GraphNode<AABB> nodeB;

                                @BeforeEach
                                void setup() {
                                    nodeB = graphMap.get(TOP).getCopyOfNodes().stream()
                                            .filter(node -> node.getContent().equals(boxB))
                                            .findFirst()
                                            .orElseThrow(() -> new IllegalStateException("Node for boxB not found"));

                                    graphMap.get(TOP).removeNode(nodeB);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}