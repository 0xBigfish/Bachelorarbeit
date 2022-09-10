package org.example.SequenceFinder.Model.Graph;

import org.example.SequenceFinder.OperatingDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.example.SequenceFinder.OperatingDirection.*;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Nested
    @DisplayName("given a graph with a single set of edges")
    class SingleEdgeSet {

        OperatingDirection opDir = OperatingDirection.FRONT;
        Set<OperatingDirection> opDirSet = new HashSet<>();


        @Nested
        @DisplayName("given an empty graph")
        class EmptyGraph {

            Graph<Integer> graph;

            @BeforeEach
            void setup() {
                opDirSet.add(opDir);
                graph = new Graph<>(opDirSet);
            }

            @Test
            @DisplayName("then the set of nodes is empty")
            void nodesEmpty() {
                assertTrue(graph.getCopyOfNodes().isEmpty());
            }

            @Test
            @DisplayName("then the set of removable nodes is empty")
            void removableEmpty() {
                assertTrue(graph.getCopyOfRemovableNodes().isEmpty());
            }

            @Test
            @DisplayName("then an IllegalGraphStateException is thrown when trying to remove a node")
            void exceptionWhenRemoving() {
                GraphNode<Integer> node = new GraphNode<>(1);
                assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(node));
            }

            @Nested
            @DisplayName("when a node is added")
            class OneNode {

                GraphNode<Integer> firstNode;

                @BeforeEach
                void setup() {
                    firstNode = graph.addNode(1);
                }

                @Test
                @DisplayName("then the set of nodes contains exactly the added object")
                void nodesContainObject() {
                    assertAll(
                            () -> assertEquals(1, graph.getCopyOfNodes().size()),
                            () -> assertTrue(graph.getCopyOfNodes().contains(firstNode))
                    );
                }

                @Test
                @DisplayName("then the set of removable nodes contains exactly the added object")
                void removableContainObject() {
                    assertAll(
                            () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                            () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode))
                    );
                }

                @Nested
                @DisplayName("when another node is added")
                class AnotherNode {

                    GraphNode<Integer> secondNode;

                    @BeforeEach
                    void setup() {
                        secondNode = graph.addNode(2);
                    }

                    @Test
                    @DisplayName("then the set of nodes contains exactly the two added objects")
                    void nodesContainExactlyBothObjects() {
                        assertAll(
                                () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                        );
                    }

                    @Test
                    @DisplayName("then the set of removable nodes contains exactly the two added objects")
                    void removableContainExactlyBothObjects() {
                        assertAll(
                                () -> assertEquals(2, graph.getCopyOfRemovableNodes().size()),
                                () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode)),
                                () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode))
                        );
                    }

                    @Test
                    @DisplayName("then an IllegalGraphStateException is thrown when adding a direct edge to a " +
                            "non-existing node")
                    void edgeToNonExistingNode() {
                        GraphNode<Integer> nonExistingNode = new GraphNode<>(3);
                        assertAll(
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> graph.addDirectedEdge(firstNode, nonExistingNode, opDir)),
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> graph.addDirectedEdge(nonExistingNode, secondNode, opDir))
                        );
                    }

                    @Test
                    @DisplayName("then an IllegalGraphStateException is thrown when adding an edge for a " +
                            "OperatingDirection not in the set of OperatingDirections")
                    void illegalOperatingDirection() {
                        assertThrows(IllegalGraphStateException.class,
                                () -> graph.addDirectedEdge(firstNode, secondNode, RIGHT));
                    }

                    @Nested
                    @DisplayName("when the first node has an outgoing edge to the second node")
                    class EdgeFromFirstToSecond {

                        @BeforeEach
                        void setup() {
                            graph.addDirectedEdge(firstNode, secondNode, opDir);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the two added objects")
                        void nodesContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes only contains the first object")
                        void removableContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("then the first node has an outgoing edge to the second node")
                        void firstNodeOneOutgoing() {
                            assertAll(
                                    () -> assertEquals(1, firstNode.getCopyOfOutgoingNodes().size()),
                                    () -> assertTrue(firstNode.getCopyOfOutgoingNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the second node has no outgoing edges")
                        void secondNodeNoOutgoing() {
                            assertFalse(secondNode.hasOutgoingEdges(null));
                        }

                        @Test
                        @DisplayName("then the first node has no incoming edges")
                        void firstNodeNoIncoming() {
                            assertFalse(firstNode.hasIncomingEdges(null));
                        }

                        @Test
                        @DisplayName("then the second node has an incoming edge from the first node")
                        void secondNodeOneIncoming() {
                            assertAll(
                                    () -> assertEquals(1, secondNode.getCopyOfIncomingNodes().size()),
                                    () -> assertTrue(secondNode.getCopyOfIncomingNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("then the second node can not be removed")
                        void secondNotRemovable() {
                            assertFalse(graph.isRemovable(secondNode));
                        }

                        @Test
                        @DisplayName("then an IllegalGraphOperationException is thrown when trying to remove the " +
                                "second node")
                        void secondNodeException() {
                            assertThrows(IllegalGraphStateException.class,
                                    () -> graph.removeNode(secondNode));
                        }

                        @Test
                        @DisplayName("then the first node can be removed")
                        void firstNodeRemovable() {
                            assertTrue(graph.isRemovable(firstNode));
                        }

                        @Nested
                        @DisplayName("when the first node is removed")
                        class FirstNodeRemoved {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(firstNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes contains exactly the second object")
                            void nodesContainOnlySecondObject() {
                                assertAll(
                                        () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                        () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                                );
                            }

                            @Test
                            @DisplayName("then the second node is removable")
                            void secondNodeRemovable() {
                                assertTrue(graph.isRemovable(secondNode));
                            }

                            @Test
                            @DisplayName("then the second node has no incoming edges")
                            void secondNodeNoIncoming() {
                                assertFalse(secondNode.hasIncomingEdges(null));
                            }

                            @Test
                            @DisplayName("then the second node has no outgoing edges")
                            void secondNodeNoOutgoing() {
                                assertFalse(secondNode.hasOutgoingEdges(null));
                            }
                        }
                    }


                    @Nested
                    @DisplayName("when the second node has an outgoing edge to the first node")
                    class EdgeFromSecondToFirst {

                        @BeforeEach
                        void setup() {
                            graph.addDirectedEdge(secondNode, firstNode, opDir);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the two added objects")
                        void nodesContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes only contains the second object")
                        void removableContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the first node has one incoming edges")
                        void firstNodeOneIncoming() {
                            assertAll(
                                    () -> assertEquals(1, firstNode.getCopyOfIncomingNodes().size()),
                                    () -> assertTrue(firstNode.getCopyOfIncomingNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the first node has no outgoing edges")
                        void firstNodeNoOutgoing() {
                            assertFalse(firstNode.hasOutgoingEdges(null));
                        }

                        @Test
                        @DisplayName("then the second node has an outgoing edge to the first node")
                        void secondNodeOneOutgoing() {
                            assertAll(
                                    () -> assertEquals(1, secondNode.getCopyOfOutgoingNodes().size()),
                                    () -> assertTrue(secondNode.getCopyOfOutgoingNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("then the second node has no incoming edge from the first node")
                        void secondNodeNoIncoming() {
                            assertFalse(secondNode.hasIncomingEdges(null));
                        }

                        @Test
                        @DisplayName("then the first node can not be removed")
                        void firstNotRemovable() {
                            assertFalse(graph.isRemovable(firstNode));
                        }

                        @Test
                        @DisplayName("then an IllegalGraphOperationException is thrown when trying to remove the " +
                                "first node")
                        void firstNodeException() {
                            assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(firstNode));
                        }

                        @Test
                        @DisplayName("then the second node can be removed")
                        void secondNodeRemovable() {
                            assertTrue(graph.isRemovable(secondNode));
                        }

                        @Nested
                        @DisplayName("when the second node is removed")
                        class SecondNodeRemoved {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(secondNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes contains exactly the first object")
                            void nodesContainOnlyFirstObject() {
                                assertAll(
                                        () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                        () -> assertTrue(graph.getCopyOfNodes().contains(firstNode))
                                );
                            }

                            @Test
                            @DisplayName("then the first node is removable")
                            void firstNodeRemovable() {
                                assertTrue(graph.isRemovable(firstNode));
                            }

                            @Test
                            @DisplayName("then the first node has no incoming edges")
                            void firstNodeNoIncoming() {
                                assertFalse(firstNode.hasIncomingEdges(null));
                            }

                            @Test
                            @DisplayName("then the first node has no outgoing edges")
                            void firstNodeNoOutgoing() {
                                assertFalse(firstNode.hasOutgoingEdges(null));
                            }
                        }
                    }


                    @Nested
                    @DisplayName("when the first node is removed")
                    class RemoveFirstNode {

                        @BeforeEach
                        void setup() {
                            graph.removeNode(firstNode);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the second object")
                        void nodesContainOnlySecond() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes contains exactly the second object")
                        void removableContainOnlySecond() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("when the first node is removed again an IllegalGraphStateException is thrown")
                        void removeFirstNodeAgain() {
                            assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(firstNode));
                        }

                        @Nested
                        @DisplayName("when the second node is removed")
                        class RemoveSecondNode {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(secondNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes is empty")
                            void nodesEmpty() {
                                assertTrue(graph.getCopyOfNodes().isEmpty());
                            }

                            @Test
                            @DisplayName("then the set of removable nodes is empty")
                            void removableEmpty() {
                                assertTrue(graph.getCopyOfRemovableNodes().isEmpty());
                            }
                        }
                    }


                    @Nested
                    @DisplayName("when the second node is removed")
                    class RemoveSecondNode {

                        @BeforeEach
                        void setup() {
                            graph.removeNode(secondNode);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the first object")
                        void nodesContainOnlyFirst() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes contains exactly the first object")
                        void removableContainOnlyFirst() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("when the second node is removed again then an IllegalGraphOperationException " +
                                "is thrown")
                        void removeSecondNodeAgain() {
                            assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(secondNode));
                        }

                        @Nested
                        @DisplayName("when the first node is removed")
                        class RemoveFirstNode {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(firstNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes is empty")
                            void nodesEmpty() {
                                assertTrue(graph.getCopyOfNodes().isEmpty());
                            }

                            @Test
                            @DisplayName("then the set of removable nodes is empty")
                            void removableEmpty() {
                                assertTrue(graph.getCopyOfRemovableNodes().isEmpty());
                            }
                        }
                    }
                }
            }
        }
    }


    @Nested
    @DisplayName("given a graph with multiple sets of edges, one for FRONT direction and one for LEFT direction")
    class FAndLEdgeSets {

        Set<OperatingDirection> opDirs = new HashSet<>();

        @BeforeEach
        void setup() {
            opDirs.add(FRONT);
            opDirs.add(LEFT);
        }

        /**
         * Assert that the node has no incoming edges for any OperatingDirection.
         *
         * @param node the node that will be checked
         * @param <T>  the type of content the node represents
         */
        private <T> void assertNoIncomingEdges(GraphNode<T> node) {
            assertFalse(node.hasIncomingEdges(null));
        }

        /**
         * Assert that the node has no outgoing edges for any OperatingDirection.
         *
         * @param node the node that will be checked
         * @param <T>  the type of content the node represents
         */
        private <T> void assertNoOutgoingEdges(GraphNode<T> node) {
            assertFalse(node.hasOutgoingEdges(null));
        }

        /**
         * Assert that the <i>from</i> node has an outgoing edge to the <i>to</i> node, and the <i>to</i> node has an
         * incoming edge from the <i>from</i> node, for the given OperatingDirection.
         *
         * @param from      the node that has the outgoing edge
         * @param to        the node that has the incoming edge
         * @param direction the OperatingDirection for which the edges are checked
         * @param <T>       the type of content the nodes represent
         */
        private <T> void assertEdgeFromTo(GraphNode<T> from, GraphNode<T> to, OperatingDirection direction) {
            assertTrue(from.hasOutgoingEdges(direction));
            assertTrue(to.hasIncomingEdges(direction));
        }


        @Nested
        @DisplayName("given an empty graph")
        class EmptyGraph {

            Graph<Integer> graph;

            @BeforeEach
            void setup() {
                graph = new Graph<>(opDirs);
            }

            @Test
            @DisplayName("then the set of nodes is empty")
            void nodesEmpty() {
                assertTrue(graph.getCopyOfNodes().isEmpty());
            }

            @Test
            @DisplayName("then the set of removable nodes is empty")
            void removableEmpty() {
                assertTrue(graph.getCopyOfRemovableNodes().isEmpty());
            }

            /**
             * The OperatingDirection is null, because the graph is the result of a merge of multiple
             * OperatingDirections and therefore represents multiple OperatingDirections, not just a single one.
             */
            @Test
            @DisplayName("then the OperatingDirections of the graph are FRONT and LEFT")
            void opDirNull() {
                assertEquals(opDirs, graph.getOperatingDirections());
            }

            @Test
            @DisplayName("then an IllegalGraphStateException is thrown when trying to remove a node")
            void exceptionWhenRemoving() {
                GraphNode<Integer> node = new GraphNode<>(1);
                assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(node));
            }

            @Nested
            @DisplayName("when a node is added")
            class OneNode {

                GraphNode<Integer> firstNode;

                @BeforeEach
                void setup() {
                    firstNode = graph.addNode(1);
                }

                @Test
                @DisplayName("then the set of nodes contains exactly the added object")
                void nodesContainObject() {
                    assertAll(
                            () -> assertEquals(1, graph.getCopyOfNodes().size()),
                            () -> assertTrue(graph.getCopyOfNodes().contains(firstNode))
                    );
                }

                @Test
                @DisplayName("then the set of removable nodes contains exactly the added object")
                void removableContainObject() {
                    assertAll(
                            () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                            () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode))
                    );
                }

                @Nested
                @DisplayName("when another node is added")
                class AnotherNode {

                    GraphNode<Integer> secondNode;

                    @BeforeEach
                    void setup() {
                        secondNode = graph.addNode(2);
                    }

                    @Test
                    @DisplayName("then the set of nodes contains exactly the two added objects")
                    void nodesContainExactlyBothObjects() {
                        assertAll(
                                () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                        );
                    }

                    @Test
                    @DisplayName("then the set of removable nodes contains exactly the two added objects")
                    void removableContainExactlyBothObjects() {
                        assertAll(
                                () -> assertEquals(2, graph.getCopyOfRemovableNodes().size()),
                                () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode)),
                                () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode))
                        );
                    }

                    @Test
                    @DisplayName("then an IllegalGraphStateException is thrown when adding a direct edge to a " +
                            "non-existing node")
                    void edgeToNonExistingNode() {
                        GraphNode<Integer> nonExistingNode = new GraphNode<>(3);
                        assertAll(
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> graph.addDirectedEdge(firstNode, nonExistingNode, FRONT)),
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> graph.addDirectedEdge(nonExistingNode, secondNode, FRONT))
                        );
                    }

                    @Nested
                    @DisplayName("when the first node has an outgoing edge for LEFT direction to the second node")
                    class LEFTEdgeFromFirstToSecond {

                        @BeforeEach
                        void setup() {
                            graph.addDirectedEdge(firstNode, secondNode, LEFT);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the two added objects")
                        void nodesContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes contains both objects")
                        void removableContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(2, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode)),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode),
                                            "The second node should be removable from the FRONT direction")
                            );
                        }

                        @Test
                        @DisplayName("then the first node has only an outgoing edge to the second node for the LEFT " +
                                "direction")
                        void firstNodeOneOutgoing() {
                            assertAll(
                                    () -> assertEquals(1, firstNode.getCopyOfOutgoingNodes().size()),
                                    () -> assertEdgeFromTo(firstNode, secondNode, LEFT)
                            );
                        }

                        @Test
                        @DisplayName("then the second node has no outgoing edges")
                        void secondNodeNoOutgoing() {
                            assertNoOutgoingEdges(secondNode);
                        }

                        @Test
                        @DisplayName("then the first node has no incoming edges")
                        void firstNodeNoIncoming() {
                            assertNoIncomingEdges(firstNode);
                        }

                        @Test
                        @DisplayName("then the second node has an incoming edge from the first node for the LEFT " +
                                "direction")
                        void secondNodeOneIncoming() {
                            assertAll(
                                    () -> assertEquals(1, secondNode.getCopyOfIncomingNodes().size()),
                                    () -> assertEdgeFromTo(firstNode, secondNode, LEFT)
                            );
                        }

                        @Test
                        @DisplayName("then the second node can be removed")
                        void secondNotRemovable() {
                            assertTrue(graph.isRemovable(secondNode));
                        }

                        @Test
                        @DisplayName("then the first node can be removed")
                        void firstNodeRemovable() {
                            assertTrue(graph.isRemovable(firstNode));
                        }

                        @Nested
                        @DisplayName("when the first node is removed")
                        class FirstNodeRemoved {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(firstNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes contains exactly the second object")
                            void nodesContainOnlySecondObject() {
                                assertAll(
                                        () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                        () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                                );
                            }

                            @Test
                            @DisplayName("then the second node is removable")
                            void secondNodeRemovable() {
                                assertTrue(graph.isRemovable(secondNode));
                            }

                            @Test
                            @DisplayName("then the second node has no incoming edges")
                            void secondNodeNoIncoming() {
                                assertNoIncomingEdges(secondNode);
                            }

                            @Test
                            @DisplayName("then the second node has no outgoing edges")
                            void secondNodeNoOutgoing() {
                                assertNoOutgoingEdges(secondNode);
                            }
                        }
                    }


                    @Nested
                    @DisplayName("when the second node has an outgoing edge for FRONT direction to the first node")
                    class FRONTEdgeFromSecondToFirst {

                        @BeforeEach
                        void setup() {
                            graph.addDirectedEdge(secondNode, firstNode, FRONT);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the two added objects")
                        void nodesContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes contains both objects")
                        void removableContainExactlyBothObjects() {
                            assertAll(
                                    () -> assertEquals(2, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode)),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("then the first node has one incoming edges for the FRONT direction")
                        void firstNodeOneIncoming() {
                            assertAll(
                                    () -> assertEquals(1, firstNode.getCopyOfIncomingNodes().size()),
                                    () -> assertEdgeFromTo(secondNode, firstNode, FRONT)
                            );
                        }

                        @Test
                        @DisplayName("then the first node has no outgoing edges")
                        void firstNodeNoOutgoing() {
                            assertNoOutgoingEdges(firstNode);
                        }

                        @Test
                        @DisplayName("then the second node has an outgoing edge to the first node for the FRONT " +
                                "direction")
                        void secondNodeOneOutgoing() {
                            assertAll(
                                    () -> assertEquals(1, secondNode.getCopyOfOutgoingNodes().size()),
                                    () -> assertEdgeFromTo(secondNode, firstNode, FRONT)
                            );
                        }

                        @Test
                        @DisplayName("then the second node has no incoming edge from the first node")
                        void secondNodeNoIncoming() {
                            assertNoIncomingEdges(secondNode);
                        }

                        @Test
                        @DisplayName("then both nodes can be removed")
                        void bothRemovable() {
                            assertAll(
                                    () -> assertTrue(graph.isRemovable(firstNode)),
                                    () -> assertTrue(graph.isRemovable(secondNode))
                            );
                        }

                        @Nested
                        @DisplayName("when the first node has an outgoing edge to the second node for the LEFT " +
                                "direction")
                        class LEFTEdgeFromFirstToSSecond {
                            OperatingDirection opDirLEFT = LEFT;

                            @BeforeEach
                            void setup() {
                                graph.addDirectedEdge(firstNode, secondNode, opDirLEFT);
                            }

                            @Test
                            @DisplayName("then the set of nodes contains exactly the two added objects")
                            void nodesContainExactlyBothObjects() {
                                assertAll(
                                        () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                        () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                        () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                                );
                            }

                            @Test
                            @DisplayName("then the set of removable nodes contains both objects")
                            void removableContainExactlyBothObjects() {
                                assertAll(
                                        () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode)),
                                        () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode)),
                                        () -> assertTrue(graph.isRemovable(firstNode)),
                                        () -> assertTrue(graph.isRemovable(secondNode))
                                );
                            }

                            @Test
                            @DisplayName("then the first node has one incoming edge for the FRONT direction")
                            void firstNodeOneIncoming() {
                                assertAll(
                                        () -> assertEquals(1, firstNode.getCopyOfIncomingNodes().size()),
                                        () -> assertEdgeFromTo(secondNode, firstNode, FRONT)
                                );
                            }

                            @Test
                            @DisplayName("then the first node has one outgoing edge for the LEFT direction")
                            void firstNodeOneOutgoing() {
                                assertAll(
                                        () -> assertEquals(1, firstNode.getCopyOfOutgoingNodes().size()),
                                        () -> assertEdgeFromTo(firstNode, secondNode, LEFT)
                                );
                            }

                            @Test
                            @DisplayName("then the second node has an outgoing edge to the first node for the FRONT " +
                                    "direction")
                            void secondNodeOneOutgoing() {
                                assertAll(
                                        () -> assertEquals(1, secondNode.getCopyOfOutgoingNodes().size()),
                                        () -> assertEdgeFromTo(secondNode, firstNode, FRONT)
                                );
                            }

                            @Test
                            @DisplayName("then the second node has one incoming edge from the first node for the LEFT" +
                                    " direction")
                            void secondNodeNoIncoming() {
                                assertAll(
                                        () -> assertEquals(1, secondNode.getCopyOfOutgoingNodes().size()),
                                        () -> assertEdgeFromTo(firstNode, secondNode, LEFT)
                                );
                            }

                            @Test
                            @DisplayName("then both nodes can be removed")
                            void bothRemovable() {
                                assertAll(
                                        () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode)),
                                        () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode)),
                                        () -> assertTrue(graph.isRemovable(firstNode)),
                                        () -> assertTrue(graph.isRemovable(secondNode))
                                );
                            }

                            @Nested
                            @DisplayName("when second node has an outgoing edge for the LEFT direction to the first " +
                                    "node")
                            class LEFTEdgeFromSecondToFirst {
                                @BeforeEach
                                void setup() {
                                    graph.addDirectedEdge(secondNode, firstNode, LEFT);
                                }

                                @Test
                                @DisplayName("then the set of nodes contains exactly the two added objects")
                                void nodesContainExactlyBothObjects() {
                                    assertAll(
                                            () -> assertEquals(2, graph.getCopyOfNodes().size()),
                                            () -> assertTrue(graph.getCopyOfNodes().contains(firstNode)),
                                            () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                                    );
                                }

                                @Test
                                @DisplayName("then the first node has one incoming edge for the FRONT direction and " +
                                        "one for the LEFT direction")
                                void firstNodeOneIncoming() {
                                    assertAll(
                                            () -> assertEquals(2, firstNode.getCopyOfIncomingNodes().size()),
                                            () -> assertEdgeFromTo(secondNode, firstNode, FRONT),
                                            () -> assertEdgeFromTo(secondNode, firstNode, LEFT)
                                    );
                                }

                                @Test
                                @DisplayName("then the first node has one outgoing edge for the LEFT direction")
                                void firstNodeOneOutgoing() {
                                    assertAll(
                                            () -> assertEquals(1, firstNode.getCopyOfOutgoingNodes().size()),
                                            () -> assertEdgeFromTo(firstNode, secondNode, LEFT)
                                    );
                                }

                                @Test
                                @DisplayName("then the second node has obe outgoing edge to the first node for the " +
                                        "FRONT and one for the LEFT direction")
                                void secondNodeOneOutgoing() {
                                    assertAll(
                                            () -> assertEquals(2, secondNode.getCopyOfOutgoingNodes().size()),
                                            () -> assertEdgeFromTo(secondNode, firstNode, FRONT),
                                            () -> assertEdgeFromTo(secondNode, firstNode, LEFT)
                                    );
                                }

                                @Test
                                @DisplayName("then the second node has one incoming edge from the first node for the " +
                                        "LEFT direction")
                                void secondNodeNoIncoming() {
                                    assertAll(
                                            () -> assertEquals(2, secondNode.getCopyOfOutgoingNodes().size()),
                                            () -> assertEdgeFromTo(firstNode, secondNode, LEFT)
                                    );
                                }

                                @Test
                                @DisplayName("then only the second node is removable")
                                void nonRemovable() {
                                    assertAll(
                                            () -> assertFalse(graph.getCopyOfRemovableNodes().contains(firstNode)),
                                            () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode)),
                                            () -> assertFalse(graph.isRemovable(firstNode)),
                                            () -> assertTrue(graph.isRemovable(secondNode))
                                    );
                                }

                                @Test
                                @DisplayName("then an IllegalGraphStateException is thrown when trying to remove the " +
                                        "first node")
                                void removeFirstNode() {
                                    assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(firstNode));
                                }

                                @Test
                                @DisplayName("then the set of removable nodes contains only the second node")
                                void removableSetEmpty() {
                                    assertAll(
                                            () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                                            () -> assertFalse(graph.getCopyOfRemovableNodes().contains(firstNode))
                                    );
                                }
                            }
                        }


                        @Nested
                        @DisplayName("when the second node is removed")
                        class SecondNodeRemoved {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(secondNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes contains exactly the first object")
                            void nodesContainOnlyFirstObject() {
                                assertAll(
                                        () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                        () -> assertTrue(graph.getCopyOfNodes().contains(firstNode))
                                );
                            }

                            @Test
                            @DisplayName("then the first node is removable")
                            void firstNodeRemovable() {
                                assertTrue(graph.isRemovable(firstNode));
                            }

                            @Test
                            @DisplayName("then the first node has no incoming edges")
                            void firstNodeNoIncoming() {
                                assertNoIncomingEdges(firstNode);
                            }

                            @Test
                            @DisplayName("then the first node has no outgoing edges")
                            void firstNodeNoOutgoing() {
                                assertNoOutgoingEdges(firstNode);
                            }
                        }
                    }


                    @Nested
                    @DisplayName("when the first node is removed")
                    class RemoveFirstNode {

                        @BeforeEach
                        void setup() {
                            graph.removeNode(firstNode);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the second object")
                        void nodesContainOnlySecond() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes contains exactly the second object")
                        void removableContainOnlySecond() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(secondNode))
                            );
                        }

                        @Test
                        @DisplayName("when the first node is removed again an IllegalGraphStateException is thrown")
                        void removeFirstNodeAgain() {
                            assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(firstNode));
                        }

                        @Nested
                        @DisplayName("when the second node is removed")
                        class RemoveSecondNode {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(secondNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes is empty")
                            void nodesEmpty() {
                                assertTrue(graph.getCopyOfNodes().isEmpty());
                            }

                            @Test
                            @DisplayName("then the set of removable nodes is empty")
                            void removableEmpty() {
                                assertTrue(graph.getCopyOfRemovableNodes().isEmpty());
                            }
                        }
                    }


                    @Nested
                    @DisplayName("when the second node is removed")
                    class RemoveSecondNode {

                        @BeforeEach
                        void setup() {
                            graph.removeNode(secondNode);
                        }

                        @Test
                        @DisplayName("then the set of nodes contains exactly the first object")
                        void nodesContainOnlyFirst() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfNodes().size()),
                                    () -> assertTrue(graph.getCopyOfNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("then the set of removable nodes contains exactly the first object")
                        void removableContainOnlyFirst() {
                            assertAll(
                                    () -> assertEquals(1, graph.getCopyOfRemovableNodes().size()),
                                    () -> assertTrue(graph.getCopyOfRemovableNodes().contains(firstNode))
                            );
                        }

                        @Test
                        @DisplayName("when the second node is removed again then an IllegalGraphOperationException " +
                                "is thrown")
                        void removeSecondNodeAgain() {
                            assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(secondNode));
                        }

                        @Nested
                        @DisplayName("when the first node is removed")
                        class RemoveFirstNode {

                            @BeforeEach
                            void setup() {
                                graph.removeNode(firstNode);
                            }

                            @Test
                            @DisplayName("then the set of nodes is empty")
                            void nodesEmpty() {
                                assertTrue(graph.getCopyOfNodes().isEmpty());
                            }

                            @Test
                            @DisplayName("then the set of removable nodes is empty")
                            void removableEmpty() {
                                assertTrue(graph.getCopyOfRemovableNodes().isEmpty());
                            }
                        }
                    }
                }
            }
        }
    }


}