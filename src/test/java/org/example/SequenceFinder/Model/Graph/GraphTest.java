package org.example.SequenceFinder.Model.Graph;

import org.example.SequenceFinder.OperatingDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Nested
    @DisplayName("given an empty graph")
    class EmptyGraph {

        OperatingDirection opDir = OperatingDirection.FRONT;
        Graph<Integer> graph;

        @BeforeEach
        void setup() {
            graph = new Graph<>(opDir);
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
                                    () -> graph.addDirectedEdge(firstNode, nonExistingNode)),
                            () -> assertThrows(IllegalGraphStateException.class,
                                    () -> graph.addDirectedEdge(nonExistingNode, secondNode))
                    );
                }

                @Nested
                @DisplayName("when the first node has an outgoing edge to the second node")
                class EdgeFromFirstToSecond {

                    @BeforeEach
                    void setup() {
                        graph.addDirectedEdge(firstNode, secondNode);
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
                        assertFalse(secondNode.hasOutgoingEdges());
                    }

                    @Test
                    @DisplayName("then the first node has no incoming edges")
                    void firstNodeNoIncoming() {
                        assertFalse(firstNode.hasIncomingEdges());
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
                        assertThrows(IllegalGraphStateException.class, () -> graph.removeNode(secondNode));
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
                            assertFalse(secondNode.hasIncomingEdges());
                        }

                        @Test
                        @DisplayName("then the second node has no outgoing edges")
                        void secondNodeNoOutgoing() {
                            assertFalse(secondNode.hasOutgoingEdges());
                        }
                    }
                }


                @Nested
                @DisplayName("when the second node has an outgoing edge to the first node")
                class EdgeFromSecondToFirst {

                    @BeforeEach
                    void setup() {
                        graph.addDirectedEdge(secondNode, firstNode);
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
                        assertFalse(firstNode.hasOutgoingEdges());
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
                        assertFalse(secondNode.hasIncomingEdges());
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
                            assertFalse(firstNode.hasIncomingEdges());
                        }

                        @Test
                        @DisplayName("then the first node has no outgoing edges")
                        void firstNodeNoOutgoing() {
                            assertFalse(firstNode.hasOutgoingEdges());
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