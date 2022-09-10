package org.example.SequenceFinder.Model.Graph;

import org.example.SequenceFinder.OperatingDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.example.SequenceFinder.OperatingDirection.FRONT;
import static org.example.SequenceFinder.OperatingDirection.LEFT;
import static org.junit.jupiter.api.Assertions.*;

class
GraphNodeTest {

    @Nested
    @DisplayName("Tests for nodes in a graph where there is a single set of edges")
    class SingleEdgeSet {
        // a set of FRONT edges
        OperatingDirection opDir = FRONT;
        Set<OperatingDirection> opDirSet = new HashSet<>();

        @BeforeEach
        void setup() {
            opDirSet.add(opDir);
        }

        @Test
        @DisplayName("then nodeA with content 1 should be equal to nodeB with content 1")
        void equals() {
            GraphNode<Integer> nodeA = new GraphNode<>(1);
            GraphNode<Integer> nodeB = new GraphNode<>(1);

            assertEquals(nodeA, nodeB);
        }

        @Nested
        @DisplayName("given a two nodes with content 'content1' and 'content2'")
        class TwoNodes {
            GraphNode<String> node1;
            GraphNode<String> node2;

            @BeforeEach
            void setup() {
                node1 = new GraphNode<>("content1");
                node2 = new GraphNode<>("content2");
            }

            @Test
            @DisplayName("then the content of node1 is 'content1'")
            void contentCheckNode1() {
                assertEquals("content1", node1.getContent());
            }

            @Test
            @DisplayName("then the content of node2 is 'content2'")
            void contentCheckNode2() {
                assertEquals("content2", node2.getContent());
            }

            @Test
            @DisplayName("then both nodes should have no incoming edges")
            void noIncoming() {
                assertAll(
                        () -> assertFalse(node1.hasIncomingEdges(opDir)),
                        () -> assertFalse(node2.hasIncomingEdges(opDir))
                );
            }

            @Test
            @DisplayName("then both nodes should have no outgoing edges")
            void noOutgoing() {
                assertAll(
                        () -> assertFalse(node1.hasOutgoingEdges(opDir)),
                        () -> assertFalse(node2.hasOutgoingEdges(opDir))
                );
            }

            @Nested
            @DisplayName("when node1 has a directed edge to node2")
            class Node1ToNode2 {
                @BeforeEach
                void setup() {
                    node1.addDirectedEdgeTo(node2, opDir);
                }

                @Test
                @DisplayName("then node1 should have no incoming edges")
                void node1NoIncoming() {
                    assertFalse(node1.hasIncomingEdges(opDir));
                }

                @Test
                @DisplayName("then node1 should have one outgoing edge")
                void node1OneOutgoing() {
                    assertEquals(1, node1.getCopyOfOutgoingNodes().size());
                }

                @Test
                @DisplayName("then node1's outgoing edge should be to node2")
                void node1OutgoingIsNode2() {
                    assertEquals(node2, node1.getCopyOfOutgoingNodes().iterator().next());
                }

                @Test
                @DisplayName("then node2 should have one incoming edge")
                void node2OneIncoming() {
                    assertEquals(1, node2.getCopyOfIncomingNodes().size());
                }

                @Test
                @DisplayName("then node2's incoming edge should be from node1")
                void node2IncomingFromNode1() {
                    assertEquals(node1, node2.getCopyOfIncomingNodes().iterator().next());
                }

                @Test
                @DisplayName("then node2 should have no outgoing edges")
                void node2NoOutgoing() {
                    assertFalse(node2.hasOutgoingEdges(opDir));
                }

                @Test
                @DisplayName("when trying to add the same edge again an exception should be thrown")
                void addSameEdgeAgain() {
                    assertAll(
                            () -> assertThrows(IllegalArgumentException.class,
                                    () -> node1.addDirectedEdgeTo(node2, opDir)),
                            () -> assertThrows(IllegalArgumentException.class,
                                    () -> node2.addDirectedEdgeFrom(node1, opDir))
                    );
                }

                @Nested
                @DisplayName("when removeAllEdges() is called on node1")
                class Node1RemoveAllEdges {

                    HashSet<GraphNode<String>> nowRemovable;

                    @BeforeEach
                    void setup() {
                        nowRemovable = node1.removeAllEdges(opDirSet);
                    }

                    @Test
                    @DisplayName("then node2 should now be removable")
                    void node2NowRemovable() {
                        assertTrue(nowRemovable.contains(node2));
                    }

                    @Test
                    @DisplayName("then node1 should have no incoming and outgoing edges")
                    void node1NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node1.hasIncomingEdges(opDir)),
                                () -> assertFalse(node1.hasOutgoingEdges(opDir))
                        );
                    }

                    @Test
                    @DisplayName("then node2 should have no incoming and outgoing edges")
                    void node2NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node2.hasIncomingEdges(opDir)),
                                () -> assertFalse(node2.hasOutgoingEdges(opDir))
                        );
                    }
                }


                @Nested
                @DisplayName("when removeAllEdges() is called on node2")
                class Node2RemoveAllEdges {

                    HashSet<GraphNode<String>> nowRemovable;

                    @BeforeEach
                    void setup() {
                        nowRemovable = node2.removeAllEdges(opDirSet);
                    }

                    /**
                     * node1 already was removable, because it had no incoming edges, just an outgoing edge to node2
                     */
                    @Test
                    @DisplayName("then the removability of node1 should be unchanged")
                    void node1Unchanged() {
                        assertFalse(nowRemovable.contains(node1));
                    }

                    @Test
                    @DisplayName("then node1 should have no incoming and outgoing edges")
                    void node1NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node1.hasIncomingEdges(opDir)),
                                () -> assertFalse(node1.hasOutgoingEdges(opDir))
                        );
                    }

                    @Test
                    @DisplayName("then node2 should have no incoming and outgoing edges")
                    void node2NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node2.hasIncomingEdges(opDir)),
                                () -> assertFalse(node2.hasOutgoingEdges(opDir))
                        );
                    }
                }


                @Nested
                @DisplayName("when the directed edge from node1 to node2 is removed")
                class EdgeRemoved {

                    @BeforeEach
                    void setup() {
                        node1.removeDirectedEdgeTo(node2, opDir);
                    }

                    @Test
                    @DisplayName("then node1 should have no incoming edges")
                    void node1NoIncoming() {
                        assertFalse(node1.hasIncomingEdges(opDir));
                    }

                    @Test
                    @DisplayName("then node1 should have no outgoing edges")
                    void node1NoOutgoing() {
                        assertFalse(node1.hasOutgoingEdges(opDir));
                    }

                    @Test
                    @DisplayName("then node2 should have no incoming edges")
                    void node2NoIncoming() {
                        assertFalse(node2.hasIncomingEdges(opDir));
                    }

                    @Test
                    @DisplayName("then node2 should have no outgoing edges")
                    void node2NoOutgoing() {
                        assertFalse(node2.hasOutgoingEdges(opDir));
                    }

                    @Test
                    @DisplayName("when trying to remove the same edge again an exception should be thrown")
                    void removeSameEdgeAgain() {
                        assertAll(
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> node1.removeDirectedEdgeTo(node2, opDir)),
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> node2.removeDirectedEdgeFrom(node1, opDir))
                        );
                    }
                }
            }


            @Nested
            @DisplayName("when node1 has a directed edge from node2")
            class Node1FromNode2 {
                @BeforeEach
                void setup() {
                    node1.addDirectedEdgeFrom(node2, opDir);
                }

                @Test
                @DisplayName("then node1 should have one incoming edge")
                void node1OneIncoming() {
                    assertEquals(1, node1.getCopyOfIncomingNodes().size());
                }

                @Test
                @DisplayName("then node1's incoming edge should be from node2")
                void node1IncomingFromNode2() {
                    assertEquals(node2, node1.getCopyOfIncomingNodes().iterator().next());
                }

                @Test
                @DisplayName("then node1 should have no outgoing edges")
                void node1NoOutgoing() {
                    assertFalse(node1.hasOutgoingEdges(opDir));
                }

                @Test
                @DisplayName("then node2 should have no incoming edges")
                void node2NoIncoming() {
                    assertFalse(node2.hasIncomingEdges(opDir));
                }

                @Test
                @DisplayName("then node2 should have one outgoing edge")
                void node2OneOutgoing() {
                    assertEquals(1, node2.getCopyOfOutgoingNodes().size());
                }

                @Test
                @DisplayName("then node2's outgoing edge should be to node1")
                void node2OutgoingToNode1() {
                    assertEquals(node1, node2.getCopyOfOutgoingNodes().iterator().next());
                }

                @Test
                @DisplayName("when trying to add the same edge again an exception should be thrown")
                void addSameEdgeAgain() {
                    assertAll(
                            () -> assertThrows(IllegalGraphStateException.class,
                                    () -> node1.addDirectedEdgeFrom(node2, opDir)),
                            () -> assertThrows(IllegalGraphStateException.class,
                                    () -> node2.addDirectedEdgeTo(node1, opDir))
                    );
                }

                @Nested
                @DisplayName("when removeAllEdges() is called on node1")
                class Node1RemoveAllEdges {

                    HashSet<GraphNode<String>> nowRemovable;

                    @BeforeEach
                    void setup() {
                        nowRemovable = node1.removeAllEdges(opDirSet);
                    }

                    @Test
                    @DisplayName("then the removability of node2 should be unchanged")
                    void node2Unchanged() {
                        assertFalse(nowRemovable.contains(node2));
                    }

                    @Test
                    @DisplayName("then node1 should have no incoming and outgoing edges")
                    void node1NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node1.hasIncomingEdges(opDir)),
                                () -> assertFalse(node1.hasOutgoingEdges(opDir))
                        );
                    }

                    @Test
                    @DisplayName("then node2 should have no incoming and outgoing edges")
                    void node2NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node2.hasIncomingEdges(opDir)),
                                () -> assertFalse(node2.hasOutgoingEdges(opDir))
                        );
                    }
                }


                @Nested
                @DisplayName("when removeAllEdges() is called on node2")
                class Node2RemoveAllEdges {

                    HashSet<GraphNode<String>> nowRemovable;

                    @BeforeEach
                    void setup() {
                        nowRemovable = node2.removeAllEdges(opDirSet);
                    }

                    @Test
                    @DisplayName("then node1 should be removable")
                    void node1NowRemovable() {
                        assertTrue(nowRemovable.contains(node1));
                    }

                    @Test
                    @DisplayName("then node1 should have no incoming and outgoing edges")
                    void node1NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node1.hasIncomingEdges(opDir)),
                                () -> assertFalse(node1.hasOutgoingEdges(opDir))
                        );
                    }

                    @Test
                    @DisplayName("then node2 should have no incoming and outgoing edges")
                    void node2NoIncomingNoOutgoing() {
                        assertAll(
                                () -> assertFalse(node2.hasIncomingEdges(opDir)),
                                () -> assertFalse(node2.hasOutgoingEdges(opDir))
                        );
                    }
                }


                @Nested
                @DisplayName("when the directed edge from node2 to node1 is removed")
                class EdgeRemoved {

                    @BeforeEach
                    void setup() {
                        node1.removeDirectedEdgeFrom(node2, opDir);
                    }

                    @Test
                    @DisplayName("then node1 should have no incoming edges")
                    void node1NoIncoming() {
                        assertFalse(node1.hasIncomingEdges(opDir));
                    }

                    @Test
                    @DisplayName("then node1 should have no outgoing edges")
                    void node1NoOutgoing() {
                        assertFalse(node1.hasOutgoingEdges(opDir));
                    }

                    @Test
                    @DisplayName("then node2 should have no incoming edges")
                    void node2NoIncoming() {
                        assertFalse(node2.hasIncomingEdges(opDir));
                    }

                    @Test
                    @DisplayName("then node2 should have no outgoing edges")
                    void node2NoOutgoing() {
                        assertFalse(node2.hasOutgoingEdges(opDir));
                    }

                    @Test
                    @DisplayName("when trying to remove the same edge again an exception should be thrown")
                    void removeSameEdgeAgain() {
                        assertAll(
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> node1.removeDirectedEdgeFrom(node2, opDir)),
                                () -> assertThrows(IllegalGraphStateException.class,
                                        () -> node2.removeDirectedEdgeTo(node1, opDir))
                        );
                    }
                }
            }
        }
    }


    @Nested
    @DisplayName("Tests for nodes in a graph where there are multiple sets of edges")
    class MultiEdgeSet {

        @Test
        @DisplayName("then nodeA with content 2 should be equal to nodeB with content 2")
        void equals() {
            GraphNode<Integer> nodeA = new GraphNode<>(2);
            GraphNode<Integer> nodeB = new GraphNode<>(2);

            assertEquals(nodeA, nodeB);
        }

        /**
         * Assert that the node has no incoming edges for any {@linkplain OperatingDirection}.
         *
         * @param node the node whose incoming edges are to be checked
         * @param <T>  the type of object that is stored in the node
         */
        private <T> void assertNoIncomingEdges(GraphNode<T> node) {
            assertTrue(node.getCopyOfIncomingNodes().isEmpty());
        }

        /**
         * Assert that the node has no outgoing edges for any {@linkplain OperatingDirection}s.
         *
         * @param node the node whose outgoing edges are to be checked
         * @param <T>  the type of object that is stored in the node
         */
        private <T> void assertNoOutgoingEdges(GraphNode<T> node) {
            assertTrue(node.getCopyOfOutgoingNodes().isEmpty());
        }

        /**
         * Assert that the nodes have a directed edges between them for the given {@linkplain OperatingDirection}.
         * Checks the outgoing edges of {@code from} and the incoming edges of {@code to}.
         *
         * @param from  the node from which the edge originates
         * @param to    the node where the edge ends
         * @param opDir the {@linkplain OperatingDirection} for which the edge should exist
         * @param <T>   the type of object that is stored in the nodes
         */
        private <T> void assertEdgeFromTo(GraphNode<T> from, GraphNode<T> to, OperatingDirection opDir) {
            assertAll(
                    () -> assertTrue(from.getCopyOfOutgoingNodes(opDir).contains(to),
                            "Node " + from + " should have an incoming edge from " + to + " for " + opDir),

                    () -> assertTrue(to.getCopyOfIncomingNodes(opDir).contains(from),
                            "Node " + to + " should have an outgoing edge to " + from + " for " + opDir)
            );
        }

        /**
         * Assert that the node is removable only from the given set of {@linkplain OperatingDirection}s.
         *
         * @param node            the node
         * @param removableFrom   the set of {@linkplain OperatingDirection}s for which the node should be removable
         * @param availableOpDirs all available {@linkplain OperatingDirection}s (including the ones for which the node
         *                        should not be removable)
         * @param <T>             the type of object that is stored in the node
         */
        private <T> void assertOnlyRemovableFrom(GraphNode<T> node,
                                                 Set<OperatingDirection> removableFrom,
                                                 Set<OperatingDirection> availableOpDirs) {
            for (OperatingDirection removableDir : removableFrom) {
                assertTrue(node.isRemovable(removableDir),
                        "Node " + node + " should be removable from " + removableDir);
            }

            availableOpDirs.removeAll(removableFrom);
            for (OperatingDirection notRemovableDir : availableOpDirs) {
                assertFalse(node.isRemovable(notRemovableDir),
                        "Node " + node + " should not be removable from " + notRemovableDir);
            }
        }


        @Nested
        @DisplayName("given OperatingDirections FRONT and LEFT")
        class OpDirsFAndL {
            Set<OperatingDirection> availableOpDirs;

            @BeforeEach
            void setup() {
                availableOpDirs = new HashSet<>();
                availableOpDirs.add(OperatingDirection.FRONT);
                availableOpDirs.add(OperatingDirection.LEFT);
            }

            @Nested
            @DisplayName("given a two nodes with content 'content1' and 'content2'")
            class TwoNodes {
                GraphNode<String> node1;
                GraphNode<String> node2;

                @BeforeEach
                void setup() {
                    node1 = new GraphNode<>("content1");
                    node2 = new GraphNode<>("content2");
                }

                @Test
                @DisplayName("then the content of node1 is 'content1'")
                void contentCheckNode1() {
                    assertEquals("content1", node1.getContent());
                }

                @Test
                @DisplayName("then the content of node2 is 'content2'")
                void contentCheckNode2() {
                    assertEquals("content2", node2.getContent());
                }

                @Test
                @DisplayName("then both nodes should have no incoming edges for any OperatingDirection")
                void noIncoming() {
                    assertAll(
                            () -> assertNoIncomingEdges(node1),
                            () -> assertNoIncomingEdges(node2)
                    );
                }

                @Test
                @DisplayName("then both nodes should have no outgoing edges for any direction")
                void noOutgoing() {
                    assertAll(
                            () -> assertNoOutgoingEdges(node1),
                            () -> assertNoOutgoingEdges(node2)
                    );
                }

                @Test
                @DisplayName("then both nodes should be removable")
                void removable() {
                    assertAll(
                            () -> assertTrue(node1.isRemovable(availableOpDirs)),
                            () -> assertTrue(node2.isRemovable(availableOpDirs))
                    );
                }

                @Nested
                @DisplayName("when node1 has a directed edge from the FRONT to node2")
                class FRONTNode1ToNode2 {
                    OperatingDirection opDirFRONT = FRONT;

                    @BeforeEach
                    void setup() {
                        node1.addDirectedEdgeTo(node2, opDirFRONT);
                    }

                    @Test
                    @DisplayName("then node1 should have no incoming edges")
                    void node1NoIncoming() {
                        assertNoIncomingEdges(node1);
                    }

                    @Test
                    @DisplayName("then node1 should have one outgoing edge total")
                    void node1OneOutgoing() {
                        assertEquals(1, node1.getCopyOfOutgoingNodes().size());
                    }

                    @Test
                    @DisplayName("then node1's outgoing edge should be to node2 for the FRONT direction")
                    void node1OutgoingIsNode2() {
                        assertEdgeFromTo(node1, node2, opDirFRONT);
                    }

                    @Test
                    @DisplayName("then node2 should have one incoming edge")
                    void node2OneIncoming() {
                        assertEquals(1, node2.getCopyOfIncomingNodes().size());
                    }

                    @Test
                    @DisplayName("then node2's incoming edge should be from node1 for the FRONT direction")
                    void node2IncomingFromNode1() {
                        assertEdgeFromTo(node1, node2, opDirFRONT);
                    }

                    @Test
                    @DisplayName("then node2 should have no outgoing edges")
                    void node2NoOutgoing() {
                        assertNoOutgoingEdges(node2);
                    }

                    @Test
                    @DisplayName("then node1 should be removable from all directions")
                    void node1() {
                        assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                    }

                    @Test
                    @DisplayName("then node2 should only be removable from the LEFT")
                    void node2() {
                        Set<OperatingDirection> removableFrom = new HashSet<>();
                        removableFrom.add(LEFT);

                        assertOnlyRemovableFrom(node2, removableFrom, availableOpDirs);
                    }

                    @Test
                    @DisplayName("when trying to add the same edge again an exception should be thrown")
                    void addSameEdgeAgain() {
                        assertThrows(IllegalGraphStateException.class,
                                () -> node1.addDirectedEdgeTo(node2, opDirFRONT));
                    }

                    @Nested
                    @DisplayName("when removeAllEdges() is called on node1")
                    class Node1RemoveAllEdges {

                        Set<GraphNode<String>> nowRemovable;

                        @BeforeEach
                        void setup() {
                            nowRemovable = node1.removeAllEdges(availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node2 should now be removable")
                        void node2NowRemovable() {
                            assertTrue(nowRemovable.contains(node2));
                        }

                        @Test
                        @DisplayName("then node1 should have no incoming and outgoing edges")
                        void node1NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node1),
                                    () -> assertNoOutgoingEdges(node1)
                            );
                        }

                        @Test
                        @DisplayName("then node2 should have no incoming and outgoing edges")
                        void node2NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node2),
                                    () -> assertNoOutgoingEdges(node2)
                            );
                        }

                        @Test
                        @DisplayName("then node1 should be removable")
                        void node1Removable() {
                            assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node2 should be removable")
                        void node2Removable() {
                            assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                        }
                    }


                    @Nested
                    @DisplayName("when removeAllEdges() is called on node2")
                    class Node2RemoveAllEdges {

                        Set<GraphNode<String>> nowRemovable;

                        @BeforeEach
                        void setup() {
                            nowRemovable = node2.removeAllEdges(availableOpDirs);
                        }

                        /**
                         * node1 already was removable, because it had no incoming edges, just an outgoing edge to
                         * node2
                         */
                        @Test
                        @DisplayName("then the removability of node1 should be unchanged")
                        void node1Unchanged() {
                            assertAll(
                                    () -> assertFalse(nowRemovable.contains(node1)),
                                    () -> assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs)
                            );
                        }

                        @Test
                        @DisplayName("then node1 should be removable")
                        void node1Removable() {
                            assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node2 should be removable")
                        void node2Removable() {
                            assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node1 should have no incoming and outgoing edges")
                        void node1NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node1),
                                    () -> assertNoOutgoingEdges(node1)
                            );
                        }

                        @Test
                        @DisplayName("then node2 should have no incoming and outgoing edges")
                        void node2NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node2),
                                    () -> assertNoOutgoingEdges(node2)
                            );
                        }
                    }


                    @Nested
                    @DisplayName("when the directed edge from node1 to node2 is removed")
                    class EdgeRemoved {

                        @BeforeEach
                        void setup() {
                            node1.removeDirectedEdgeTo(node2, opDirFRONT);
                        }

                        @Test
                        @DisplayName("then node1 should have no incoming and outgoing edges")
                        void node1NoIncoming() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node1),
                                    () -> assertNoOutgoingEdges(node1)
                            );
                        }

                        @Test
                        @DisplayName("then node1 should be removable")
                        void node1Removable() {
                            assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node2 should have no incoming and outgoing edges")
                        void node2NoIncoming() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node2),
                                    () -> assertNoOutgoingEdges(node2)
                            );
                        }

                        @Test
                        @DisplayName("then node2 should be removable")
                        void node2Removable() {
                            assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("when trying to remove the same edge again an exception should be thrown")
                        void removeSameEdgeAgain() {
                            assertThrows(IllegalGraphStateException.class,
                                    () -> node1.removeDirectedEdgeTo(node2, opDirFRONT));
                        }
                    }
                }


                @Nested
                @DisplayName("when node2 has a directed edge to node1 for the LEFT direction")
                class LEFTNode1FromNode2 {
                    OperatingDirection opDirLEFT = LEFT;

                    @BeforeEach
                    void setup() {
                        node1.addDirectedEdgeFrom(node2, opDirLEFT);
                    }

                    @Test
                    @DisplayName("then node1 should have one incoming edge")
                    void node1OneIncoming() {
                        assertEquals(1, node1.getCopyOfIncomingNodes().size());
                    }

                    @Test
                    @DisplayName("then node1's incoming edge should be from node2")
                    void node1IncomingFromNode2() {
                        assertEdgeFromTo(node2, node1, opDirLEFT);
                    }

                    @Test
                    @DisplayName("then node1 should have no outgoing edges")
                    void node1NoOutgoing() {
                        assertNoOutgoingEdges(node1);
                    }

                    @Test
                    @DisplayName("then node2 should have no incoming edges")
                    void node2NoIncoming() {
                        assertNoIncomingEdges(node2);
                    }

                    @Test
                    @DisplayName("then node2 should have one outgoing edge")
                    void node2OneOutgoing() {
                        assertEquals(1, node2.getCopyOfOutgoingNodes().size());
                    }

                    @Test
                    @DisplayName("then node2's outgoing edge should be to node1 for the LEFT direction")
                    void node2OutgoingToNode1() {
                        assertEdgeFromTo(node2, node1, opDirLEFT);
                    }

                    @Test
                    @DisplayName("when trying to add the same edge again an exception should be thrown")
                    void addSameEdgeAgain() {
                        assertThrows(IllegalGraphStateException.class,
                                () -> node1.addDirectedEdgeFrom(node2, opDirLEFT));
                    }

                    @Test
                    @DisplayName("then node1 should only be removable from the FRONT direction")
                    void node1Removable() {
                        Set<OperatingDirection> removableFrom = new HashSet<>();
                        removableFrom.add(FRONT);

                        assertOnlyRemovableFrom(node1, removableFrom, availableOpDirs);
                    }

                    @Test
                    @DisplayName("then node2 should be removable from the FRONT and LEFT direction")
                    void node2Removable() {
                        Set<OperatingDirection> removableFrom = new HashSet<>();
                        removableFrom.add(FRONT);
                        removableFrom.add(LEFT);

                        assertOnlyRemovableFrom(node2, removableFrom, availableOpDirs);
                    }

                    @Nested
                    @DisplayName("when removeAllEdges() is called on node1")
                    class Node1RemoveAllEdges {

                        Set<GraphNode<String>> nowRemovable;

                        @BeforeEach
                        void setup() {
                            nowRemovable = node1.removeAllEdges(availableOpDirs);
                        }

                        @Test
                        @DisplayName("then the removability of node2 should be unchanged")
                        void node2Unchanged() {
                            assertAll(
                                    () -> assertFalse(nowRemovable.contains(node2)),
                                    () -> assertTrue(node2.isRemovable(availableOpDirs))
                            );
                        }

                        @Test
                        @DisplayName("then node1 should have no incoming and outgoing edges")
                        void node1NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node1),
                                    () -> assertNoOutgoingEdges(node1)
                            );
                        }

                        @Test
                        @DisplayName("then node2 should have no incoming and outgoing edges")
                        void node2NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node2),
                                    () -> assertNoOutgoingEdges(node2)
                            );
                        }

                        @Test
                        @DisplayName("then node1 should be removable")
                        void node1Removable() {
                            assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node2 should be removable")
                        void node2Removable() {
                            assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                        }
                    }


                    @Nested
                    @DisplayName("when removeAllEdges() is called on node2")
                    class Node2RemoveAllEdges {

                        Set<GraphNode<String>> nowRemovable;

                        @BeforeEach
                        void setup() {
                            nowRemovable = node2.removeAllEdges(availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node1 should have become removable")
                        void node1NowRemovable() {
                            assertAll(
                                    () -> assertTrue(nowRemovable.contains(node1)),
                                    () -> assertTrue(node1.isRemovable(availableOpDirs))
                            );
                        }

                        @Test
                        @DisplayName("then node1 should be removable")
                        void node1Removable() {
                            assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node2 should be removable")
                        void node2Removable() {
                            assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node1 should have no incoming and outgoing edges")
                        void node1NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node1),
                                    () -> assertNoOutgoingEdges(node1)
                            );
                        }

                        @Test
                        @DisplayName("then node2 should have no incoming and outgoing edges")
                        void node2NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node2),
                                    () -> assertNoOutgoingEdges(node2)
                            );
                        }
                    }


                    @Nested
                    @DisplayName("when the directed edge from node2 to node1 is removed")
                    class EdgeRemoved {

                        @BeforeEach
                        void setup() {
                            node1.removeDirectedEdgeFrom(node2, opDirLEFT);
                        }

                        @Test
                        @DisplayName("then node1 should have no incoming and outgoing edges")
                        void node1NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node1),
                                    () -> assertNoOutgoingEdges(node1)
                            );
                        }

                        @Test
                        @DisplayName("then node1 should have no incoming and outgoing edges")
                        void node2NoIncomingNoOutgoing() {
                            assertAll(
                                    () -> assertNoIncomingEdges(node2),
                                    () -> assertNoOutgoingEdges(node2)
                            );
                        }

                        @Test
                        @DisplayName("then node1 should be removable")
                        void node1Removable() {
                            assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node2 should be removable")
                        void node2Removable() {
                            assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                        }

                        @Test
                        @DisplayName("when trying to remove the same edge again an exception should be thrown")
                        void removeSameEdgeAgain() {
                            assertThrows(IllegalGraphStateException.class,
                                    () -> node1.removeDirectedEdgeFrom(node2, opDirLEFT));
                        }
                    }


                    @Nested
                    @DisplayName("when an additional directed edge from node1 to node2 for the FRONT direction " +
                            "is added")
                    class FRONTNode1ToNode2 {
                        OperatingDirection opDirFRONT = FRONT;

                        @BeforeEach
                        void setup() {
                            node1.addDirectedEdgeTo(node2, opDirFRONT);
                        }

                        @Test
                        @DisplayName("then node1 should have one incoming edge")
                        void node1NoIncoming() {
                            assertEquals(1, node1.getCopyOfOutgoingNodes().size());
                        }

                        @Test
                        @DisplayName("then node1's incoming edge should be from node2 for the LEFT direction")
                        void node1EdgeFromNode2() {
                            assertEdgeFromTo(node2, node1, opDirLEFT);
                        }

                        @Test
                        @DisplayName("then node1 should have one outgoing edge total")
                        void node1OneOutgoing() {
                            assertEquals(1, node1.getCopyOfOutgoingNodes().size());
                        }

                        @Test
                        @DisplayName("then node1 should only be removable from the FRONT direction")
                        void node1Removable() {
                            Set<OperatingDirection> removableFrom = new HashSet<>();
                            removableFrom.add(FRONT);

                            assertOnlyRemovableFrom(node1, removableFrom, availableOpDirs);
                        }

                        @Test
                        @DisplayName("then node1's outgoing edge should be to node2 for the FRONT direction")
                        void node1OutgoingIsNode2() {
                            assertEdgeFromTo(node1, node2, opDirFRONT);
                        }

                        @Test
                        @DisplayName("then node2 should have one incoming edge")
                        void node2OneIncoming() {
                            assertEquals(1, node2.getCopyOfIncomingNodes().size());
                        }

                        @Test
                        @DisplayName("then node2's incoming edge should be from node1 for the FRONT direction")
                        void node2IncomingFromNode1() {
                            assertEdgeFromTo(node1, node2, opDirFRONT);
                        }

                        @Test
                        @DisplayName("then node2 should have one outgoing edges")
                        void node2NoOutgoing() {
                            assertEquals(1, node2.getCopyOfOutgoingNodes().size());
                        }

                        @Test
                        @DisplayName("then node2's outgoing edge should be to node1 for the LEFT direction")
                        void node2Outgoing() {
                            assertEdgeFromTo(node2, node1, opDirLEFT);
                        }

                        @Test
                        @DisplayName("then node 2 should be only removable from the LEFT direction")
                        void node2Removable() {
                            Set<OperatingDirection> removableFrom = new HashSet<>();
                            removableFrom.add(LEFT);

                            assertOnlyRemovableFrom(node2, removableFrom, availableOpDirs);
                        }

                        @Test
                        @DisplayName("when trying to add the same edge again an exception should be thrown")
                        void addSameEdgeAgain() {
                            assertThrows(IllegalGraphStateException.class,
                                    () -> node1.addDirectedEdgeTo(node2, opDirFRONT));
                        }

                        @Nested
                        @DisplayName("when removeAllEdges() is called on node1")
                        class Node1RemoveAllEdges {

                            Set<GraphNode<String>> nowRemovable;

                            @BeforeEach
                            void setup() {
                                nowRemovable = node1.removeAllEdges(availableOpDirs);
                            }

                            @Test
                            @DisplayName("then node2 should now be removable from all directions")
                            void node2NowRemovable() {
                                assertAll(
                                        () -> assertTrue(nowRemovable.contains(node2)),
                                        () -> assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs)
                                );
                            }

                            @Test
                            @DisplayName("then node1 should be removable from all directions")
                            void node1Removable() {
                                assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs);
                            }

                            @Test
                            @DisplayName("then node1 should have no incoming and outgoing edges")
                            void node1NoIncomingNoOutgoing() {
                                assertAll(
                                        () -> assertNoIncomingEdges(node1),
                                        () -> assertNoOutgoingEdges(node1)
                                );
                            }

                            @Test
                            @DisplayName("then node2 should have no incoming and outgoing edges")
                            void node2NoIncomingNoOutgoing() {
                                assertAll(
                                        () -> assertNoIncomingEdges(node2),
                                        () -> assertNoOutgoingEdges(node2)
                                );
                            }
                        }


                        @Nested
                        @DisplayName("when removeAllEdges() is called on node2")
                        class Node2RemoveAllEdges {

                            Set<GraphNode<String>> nowRemovable;

                            @BeforeEach
                            void setup() {
                                nowRemovable = node2.removeAllEdges(availableOpDirs);
                            }


                            @Test
                            @DisplayName("then the node1 should now be removable from all directions")
                            void node1Removable() {
                                assertAll(
                                        () -> assertTrue(nowRemovable.contains(node1)),
                                        () -> assertOnlyRemovableFrom(node1, availableOpDirs, availableOpDirs)
                                );
                            }

                            @Test
                            @DisplayName("then node2 should be removable from all directions")
                            void node2Removable() {
                                assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                            }

                            @Test
                            @DisplayName("then node1 should have no incoming and outgoing edges")
                            void node1NoIncomingNoOutgoing() {
                                assertAll(
                                        () -> assertNoIncomingEdges(node1),
                                        () -> assertNoOutgoingEdges(node1)
                                );
                            }

                            @Test
                            @DisplayName("then node2 should have no incoming and outgoing edges")
                            void node2NoIncomingNoOutgoing() {
                                assertAll(
                                        () -> assertNoIncomingEdges(node2),
                                        () -> assertNoOutgoingEdges(node2)
                                );
                            }
                        }


                        @Nested
                        @DisplayName("when the directed edge from node1 to node2 is removed")
                        class EdgeN1ToN2Removed {

                            @BeforeEach
                            void setup() {
                                node1.removeDirectedEdgeTo(node2, opDirFRONT);
                            }

                            @Test
                            @DisplayName("then node1 should have one incoming edges")
                            void node1OneIncoming() {
                                assertEquals(1, node1.getCopyOfIncomingNodes().size());
                            }

                            @Test
                            @DisplayName("then node1's incoming edge should be from node2 for the LEFT direction")
                            void node1OneIncomingLEFT() {
                                assertEdgeFromTo(node2, node1, opDirLEFT);
                            }

                            @Test
                            @DisplayName("then node1 should have no outgoing edges")
                            void node1NoOutgoing() {
                                assertNoOutgoingEdges(node1);
                            }

                            @Test
                            @DisplayName("then node1 should be only removable from the FRONT directions")
                            void node1Removable() {
                                Set<OperatingDirection> removableFrom = new HashSet<>();
                                removableFrom.add(FRONT);

                                assertOnlyRemovableFrom(node1, removableFrom, availableOpDirs);
                            }

                            @Test
                            @DisplayName("then node2 should have no incoming edges")
                            void node2NoIncoming() {
                                assertNoIncomingEdges(node2);
                            }

                            @Test
                            @DisplayName("then node2 should have one outgoing edge")
                            void node2OneOutgoing() {
                                assertEquals(1, node2.getCopyOfOutgoingNodes().size());
                            }

                            @Test
                            @DisplayName("then node2's outgoing edge should be to node1 for the LEFT direction")
                            void node2OneToNode1() {
                                assertEdgeFromTo(node2, node1, opDirLEFT);
                            }

                            @Test
                            @DisplayName("then node2 should be removable from all directions")
                            void node2Removable() {
                                assertOnlyRemovableFrom(node2, availableOpDirs, availableOpDirs);
                            }

                            @Test
                            @DisplayName("when trying to remove the same edge again an exception should be thrown")
                            void removeSameEdgeAgain() {
                                assertThrows(IllegalGraphStateException.class,
                                        () -> node1.removeDirectedEdgeTo(node2, opDirFRONT));
                            }
                        }
                    }
                }
            }
        }
    }
}