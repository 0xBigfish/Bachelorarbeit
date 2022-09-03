package org.example.SequenceFinder.Model.Graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodeTest {

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
                    () -> assertEquals(0, node1.getCopyOfIncomingNodes().size()),
                    () -> assertEquals(0, node2.getCopyOfIncomingNodes().size())
            );
        }

        @Test
        @DisplayName("then both nodes should have no outgoing edges")
        void noOutgoing() {
            assertAll(
                    () -> assertEquals(0, node1.getCopyOfOutgoingNodes().size()),
                    () -> assertEquals(0, node2.getCopyOfOutgoingNodes().size())
            );
        }

        @Nested
        @DisplayName("when node1 has a directed edge to node2")
        class Node1ToNode2 {
            @BeforeEach
            void setup() {
                node1.addDirectedEdgeTo(node2);
            }

            @Test
            @DisplayName("then node1 should have no incoming edges")
            void node1NoIncoming() {
                assertEquals(0, node1.getCopyOfIncomingNodes().size());
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
                assertEquals(0, node2.getCopyOfOutgoingNodes().size());
            }

            @Test
            @DisplayName("when trying to add the same edge again an exception should be thrown")
            void addSameEdgeAgain() {
                assertThrows(IllegalGraphStateException.class, () -> node1.addDirectedEdgeTo(node2));
            }

            @Nested
            @DisplayName("when removeAllEdges() is called on node1")
            class Node1RemoveAllEdges {

                HashSet<GraphNode<String>> nowRemovable;

                @BeforeEach
                void setup() {
                    nowRemovable = node1.removeAllEdges();
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
                            () -> assertEquals(0, node1.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node1.getCopyOfOutgoingNodes().size())
                    );
                }

                @Test
                @DisplayName("then node2 should have no incoming and outgoing edges")
                void node2NoIncomingNoOutgoing() {
                    assertAll(
                            () -> assertEquals(0, node2.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node2.getCopyOfOutgoingNodes().size())
                    );
                }
            }


            @Nested
            @DisplayName("when removeAllEdges() is called on node2")
            class Node2RemoveAllEdges {

                HashSet<GraphNode<String>> nowRemovable;

                @BeforeEach
                void setup() {
                    nowRemovable = node2.removeAllEdges();
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
                            () -> assertEquals(0, node1.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node1.getCopyOfOutgoingNodes().size())
                    );
                }

                @Test
                @DisplayName("then node2 should have no incoming and outgoing edges")
                void node2NoIncomingNoOutgoing() {
                    assertAll(
                            () -> assertEquals(0, node2.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node2.getCopyOfOutgoingNodes().size())
                    );
                }
            }


            @Nested
            @DisplayName("when the directed edge from node1 to node2 is removed")
            class EdgeRemoved {

                @BeforeEach
                void setup() {
                    node1.removeDirectedEdgeTo(node2);
                }

                @Test
                @DisplayName("then node1 should have no incoming edges")
                void node1NoIncoming() {
                    assertEquals(0, node1.getCopyOfIncomingNodes().size());
                }

                @Test
                @DisplayName("then node1 should have no outgoing edges")
                void node1NoOutgoing() {
                    assertEquals(0, node1.getCopyOfOutgoingNodes().size());
                }

                @Test
                @DisplayName("then node2 should have no incoming edges")
                void node2NoIncoming() {
                    assertEquals(0, node2.getCopyOfIncomingNodes().size());
                }

                @Test
                @DisplayName("then node2 should have no outgoing edges")
                void node2NoOutgoing() {
                    assertEquals(0, node2.getCopyOfOutgoingNodes().size());
                }

                @Test
                @DisplayName("when trying to remove the same edge again an exception should be thrown")
                void removeSameEdgeAgain() {
                    assertThrows(IllegalGraphStateException.class, () -> node1.removeDirectedEdgeTo(node2));
                }
            }
        }


        @Nested
        @DisplayName("when node1 has a directed edge from node2")
        class Node1FromNode2 {
            @BeforeEach
            void setup() {
                node1.addDirectedEdgeFrom(node2);
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
                assertEquals(0, node1.getCopyOfOutgoingNodes().size());
            }

            @Test
            @DisplayName("then node2 should have no incoming edges")
            void node2NoIncoming() {
                assertEquals(0, node2.getCopyOfIncomingNodes().size());
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
                assertThrows(IllegalGraphStateException.class, () -> node1.addDirectedEdgeFrom(node2));
            }

            @Nested
            @DisplayName("when removeAllEdges() is called on node1")
            class Node1RemoveAllEdges {

                HashSet<GraphNode<String>> nowRemovable;

                @BeforeEach
                void setup() {
                    nowRemovable = node1.removeAllEdges();
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
                            () -> assertEquals(0, node1.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node1.getCopyOfOutgoingNodes().size())
                    );
                }

                @Test
                @DisplayName("then node2 should have no incoming and outgoing edges")
                void node2NoIncomingNoOutgoing() {
                    assertAll(
                            () -> assertEquals(0, node2.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node2.getCopyOfOutgoingNodes().size())
                    );
                }
            }


            @Nested
            @DisplayName("when removeAllEdges() is called on node2")
            class Node2RemoveAllEdges {

                HashSet<GraphNode<String>> nowRemovable;

                @BeforeEach
                void setup() {
                    nowRemovable = node2.removeAllEdges();
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
                            () -> assertEquals(0, node1.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node1.getCopyOfOutgoingNodes().size())
                    );
                }

                @Test
                @DisplayName("then node2 should have no incoming and outgoing edges")
                void node2NoIncomingNoOutgoing() {
                    assertAll(
                            () -> assertEquals(0, node2.getCopyOfIncomingNodes().size()),
                            () -> assertEquals(0, node2.getCopyOfOutgoingNodes().size())
                    );
                }
            }


            @Nested
            @DisplayName("when the directed edge from node2 to node1 is removed")
            class EdgeRemoved {

                @BeforeEach
                void setup() {
                    node1.removeDirectedEdgeFrom(node2);
                }

                @Test
                @DisplayName("then node1 should have no incoming edges")
                void node1NoIncoming() {
                    assertEquals(0, node1.getCopyOfIncomingNodes().size());
                }

                @Test
                @DisplayName("then node1 should have no outgoing edges")
                void node1NoOutgoing() {
                    assertEquals(0, node1.getCopyOfOutgoingNodes().size());
                }

                @Test
                @DisplayName("then node2 should have no incoming edges")
                void node2NoIncoming() {
                    assertEquals(0, node2.getCopyOfIncomingNodes().size());
                }

                @Test
                @DisplayName("then node2 should have no outgoing edges")
                void node2NoOutgoing() {
                    assertEquals(0, node2.getCopyOfOutgoingNodes().size());
                }

                @Test
                @DisplayName("when trying to remove the same edge again an exception should be thrown")
                void removeSameEdgeAgain() {
                    assertThrows(IllegalGraphStateException.class, () -> node1.removeDirectedEdgeFrom(node2));
                }
            }
        }
    }
}