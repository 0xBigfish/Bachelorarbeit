package org.example.SequenceFinder.Model.Octree;

import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.GeometricObjects.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * This class uses Behavior Driven testing.
 */
@ExtendWith(MockitoExtension.class)
public class LooseOctreeTest {

    /**
     * looseness value of the loose Octree. Some formulas need k=2, otherwise the mathematical transformation wouldn't
     * work and these formulas would be different.<br> According to Thatcher Ulrich in "Game Programming Gems (2000),
     * Loose Octrees" a value of k=2 is a good balance between loose but not too loose.
     */
    double k = 2;


    @Nested
    @DisplayName("given a LooseOctree with maxDepth = 3 and worldSize = 8")
    class MaxDepth3WorldSize8 {

        LooseOctree<AABB> looseOctree;

        int worldSize = 8;
        int maxDepth = 3;

        @BeforeEach
        void setup() {
            looseOctree = new LooseOctree<>(maxDepth, worldSize);
        }

        /**
         * Count the total number of nodes over all dimensions at a height.
         *
         * @param nodes the nodes at the given height
         * @return the total number of nodes over all dimensions
         */
        int countTotalNodes(OctreeNode<AABB>[][][] nodes) {
            int counter = 0;
            for (OctreeNode<AABB>[][] x : nodes) {
                for (OctreeNode<AABB>[] y : x) {
                    for (OctreeNode<AABB> ignored : y) {
                        counter++;
                    }
                }
            }
            return counter;
        }

        /**
         * Checks whether x, y and z dimension all hold the same number of nodes over the whole array.
         *
         * @param nodes the nodes of the loose octree
         * @return true when all three dimensions are equally sized, false otherwise
         */
        boolean equallySizedDimensions(OctreeNode<AABB>[][][] nodes) {
            // all dimensions must be equally sized, doesn't matter which one is chosen to be compared against.
            // here the x dimension is chosen.
            int referenceDimension = nodes.length;

            for (OctreeNode<AABB>[][] y : nodes) {
                if (y.length != referenceDimension) {
                    return false;
                }
                for (OctreeNode<AABB>[] z : y) {
                    if (z.length != referenceDimension) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Add the given box to the octree and assert that it has been added. Also assert that the box only contains the
         * expected content
         *
         * @param boxToAdd                         the box that will be added to the LooseOctree
         * @param nodeWhereItShouldHaveBeenAddedTo the node in the octree where the box should have been added to
         * @param expectedContent                  the expected content of the specified node
         */
        void addBoxAndAssertBoxAddedToOctree(AABB boxToAdd, OctreeNode<AABB> nodeWhereItShouldHaveBeenAddedTo,
                                             HashSet<AABB> expectedContent) {
            assertAll(
                    () -> assertTrue(looseOctree.insertObject(boxToAdd)),
                    () -> assertEquals(expectedContent, nodeWhereItShouldHaveBeenAddedTo.getContent())
            );
        }

        /**
         * Assert that each dimension at the given depth has the same number of nodes in the x, y and z dimension.
         *
         * @param givenDepth          the depth where the check is performed
         * @param numberOfNodesPerDim the expected number of nodes per dimension at the given depth
         */
        private void assertNumberOfNodesPerDimension(int givenDepth, int numberOfNodesPerDim) {
            assertAll(
                    // check x dimension
                    () -> assertEquals(numberOfNodesPerDim, looseOctree.nodes[givenDepth].length),
                    // check y dimension
                    () -> assertEquals(numberOfNodesPerDim, looseOctree.nodes[givenDepth][0].length),
                    // check z dimension
                    () -> assertEquals(numberOfNodesPerDim, looseOctree.nodes[givenDepth][0][0].length),

                    // check all dimensions are equally sized.
                    () -> assertTrue(equallySizedDimensions(looseOctree.nodes[givenDepth]))
            );
        }


        /**
         * Assert the result has the same value in the x, y and z dimensions
         *
         * @param result the result that will be checked
         */
        void assertXYAndZAreTheSame(Point result) {
            assertAll(
                    () -> assertEquals(result.x, result.y),
                    () -> assertEquals(result.y, result.z)
            );
        }

        @Nested
        @DisplayName("bounding cube tests")
        class BoundingCubeTests {

            int depth;


            @Nested
            @DisplayName("given depth of 0")
            class Depth0 {
                @BeforeEach
                void setup() {
                    depth = 0;
                }

                @Test
                @DisplayName("then the loose bounding cube of the root should be from (-8,-8,-8) to (8,8,8)")
                void boundingCube() {
                    AABB boundingCube = new AABB(new Point(-8, -8, -8), new Point(8, 8, 8));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][0][0].getAABB());
                }
            }


            @Nested
            @DisplayName("given depth of 1")
            class Depth1 {

                @BeforeEach
                void setup() {
                    depth = 1;
                }

                @Test
                @DisplayName("then the spacing to the next node should be 4")
                void spacing() {
                    Point centerFrontBotLeft = looseOctree.nodes[depth][0][0][0].getAABB().calcCenter();
                    Point centerFrontTopLeft = looseOctree.nodes[depth][0][0][1].getAABB().calcCenter();

                    assertAll(
                            () -> assertEquals(0, centerFrontTopLeft.x - centerFrontBotLeft.x),
                            () -> assertEquals(0, centerFrontTopLeft.y - centerFrontBotLeft.y),
                            () -> assertEquals(4, centerFrontTopLeft.z - centerFrontBotLeft.z)
                    );
                }

                @Test
                @DisplayName("then the loose bounding cube of node[1][0][0][0] should be from (-6,-6,-6) to (2,2,2)")
                void boundingCube1_0_0_0() {
                    AABB boundingCube = new AABB(new Point(-6, -6, -6), new Point(2, 2, 2));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][0][0].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[1][0][0][1] should be from (-6,-6,-2) to (2,2,6)")
                void boundingCube1_0_0_1() {
                    AABB boundingCube = new AABB(new Point(-6, -6, -2), new Point(2, 2, 6));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][0][1].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[1][0][1][0] should be from (-6,-2,-6) to (2,6,2)")
                void boundingCube1_0_1_0() {
                    AABB boundingCube = new AABB(new Point(-6, -2, -6), new Point(2, 6, 2));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][1][0].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[1][0][1][1] should be from (-6,-2,-2) to (2,6,6)")
                void boundingCube1_0_1_1() {
                    AABB boundingCube = new AABB(new Point(-6, -2, -2), new Point(2, 6, 6));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][1][1].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[1][1][0][0] should be from (-2,-6,-6) to (6,2,2)")
                void boundingCube1_1_0_0() {
                    AABB boundingCube = new AABB(new Point(-2, -6, -6), new Point(6, 2, 2));

                    assertEquals(boundingCube, looseOctree.nodes[depth][1][0][0].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[1][1][0][1] should be from (-2,-6,-2) to (6,2,6)")
                void boundingCube1_1_0_1() {
                    AABB boundingCube = new AABB(new Point(-2, -6, -2), new Point(6, 2, 6));

                    assertEquals(boundingCube, looseOctree.nodes[depth][1][0][1].getAABB());
                }

                @Test
                @DisplayName("then the bounding cube of node[1][1][1][0] should be from (-2,-2,-6) to (6,6,2)")
                void boundingCube1_1_1_0() {
                    AABB boundingCube = new AABB(new Point(-2, -2, -6), new Point(6, 6, 2));

                    assertEquals(boundingCube, looseOctree.nodes[depth][1][1][0].getAABB());
                }

                @Test
                @DisplayName("then the bounding cube of node[1][1][1][1] should be from (-2,-2,-2) to (6,6,6)")
                void boundingCube1_1_1_1() {
                    AABB boundingCube = new AABB(new Point(-2, -2, -2), new Point(6, 6, 6));

                    assertEquals(boundingCube, looseOctree.nodes[depth][1][1][1].getAABB());
                }
            }


            @Nested
            @DisplayName("given depth of 2")
            class Depth2 {
                @BeforeEach
                void setup() {
                    depth = 2;
                }

                @Test
                @DisplayName("then the spacing should be 2")
                void spacing() {
                    Point centerFrontBotLeft = looseOctree.nodes[depth][0][0][0].getAABB().calcCenter();
                    Point centerFrontTopLeft = looseOctree.nodes[depth][0][0][1].getAABB().calcCenter();

                    assertAll(
                            () -> assertEquals(0, centerFrontTopLeft.x - centerFrontBotLeft.x),
                            () -> assertEquals(0, centerFrontTopLeft.y - centerFrontBotLeft.y),
                            () -> assertEquals(2, centerFrontTopLeft.z - centerFrontBotLeft.z)
                    );
                }

                @Test
                @DisplayName("then the loose bounding cube of node[2][0][0][0] should be from (-5,-5,-5) to (-1,-1,-1)")
                void boundingCube2_0_0_0() {
                    AABB boundingCube = new AABB(new Point(-5, -5, -5), new Point(-1, -1, -1));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][0][0].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[2][0][0][1] should be from (-5,-5,-3) to (-1,-1,1)")
                void boundingCube2_0_0_1() {
                    AABB boundingCube = new AABB(new Point(-5, -5, -3), new Point(-1, -1, 1));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][0][1].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[2][0][0][2] should be from (-5,-5,-1) to (-1,-1,3)")
                void boundingCube2_0_0_2() {
                    AABB boundingCube = new AABB(new Point(-5, -5, -1), new Point(-1, -1, 3));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][0][2].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[2][0][0][3] should be from (-5,-5,1) to (-1,-1,5)")
                void boundingCube2_0_0_3() {
                    AABB boundingCube = new AABB(new Point(-5, -5, 1), new Point(-1, -1, 5));

                    assertEquals(boundingCube, looseOctree.nodes[depth][0][0][3].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[2][1][1][1] should be from (-3,-3,-3) to (1,1,1)")
                void boundingCube2_1_1_1() {
                    AABB boundingCube = new AABB(new Point(-3, -3, -3), new Point(1, 1, 1));

                    assertEquals(boundingCube, looseOctree.nodes[depth][1][1][1].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node[2][2][2][2] should be from (-1,-1,-1) to (3,3,3)")
                void boundingCube2_2_2_2() {
                    AABB boundingCube = new AABB(new Point(-1, -1, -1), new Point(3, 3, 3));

                    assertEquals(boundingCube, looseOctree.nodes[depth][2][2][2].getAABB());
                }

                @Test
                @DisplayName("then the loose bounding cube of node [2][3][3][3] should be from (1,1,1) to (5,5,5)")
                void boundingCube2_3_3_3() {
                    AABB boundingCube = new AABB(new Point(1, 1, 1), new Point(5, 5, 5));

                    assertEquals(boundingCube, looseOctree.nodes[depth][3][3][3].getAABB());
                }
            }
        }

        @Test
        @DisplayName("then the loose AABB of the world should be AABB((-8,-8,-8), (8,8,8))")
        void worldBox() {
            assertEquals(new AABB(new Point(-8, -8, -8), new Point(8, 8, 8)), looseOctree.getWorldAABB());
        }


        @Nested
        @DisplayName("tests based on depth")
        class DepthTests {

            @Nested
            @DisplayName("given depth of -1")
            class DepthOfNeg1 {

                int givenDepth = -1;


                @Nested
                @DisplayName("accessing nodes array tests")
                class ArrayNodesTests {

                    @Test
                    @DisplayName("then an IndexOutOfBoundsException should be thrown when trying to interact with " +
                            "any node")
                    void thenAnIndexOutOfBoundsShouldBeThrown() {
                        // access any node, as the givenDepth will result in an IndexOutOfBounds
                        assertThrows(IndexOutOfBoundsException.class,
                                () -> looseOctree.nodes[givenDepth][0][0][0].insertObject(null));
                    }
                }


                @Nested
                @DisplayName("boundingCubeLength tests")
                class BCLengthTests {

                    @Test
                    @DisplayName("then boundingCubeLength(-1) should throw an IllegalArgumentException")
                    void thenBoundingCubeLength0ShouldThrowAnException() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeLength(givenDepth));
                    }
                }


                @Nested
                @DisplayName("boundingCubeSpacing tests")
                class BCSpacingTests {

                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void thenAnIllegalArgumentExceptionShouldBeThrown() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeSpacing(givenDepth));
                    }
                }
            }


            @Nested
            @DisplayName("given depth of 0")
            class DepthOf0 {

                int givenDepth = 0;


                @Nested
                @DisplayName("accessing nodes array tests")
                class ArrayNodesTests {

                    @Test
                    @DisplayName("then there should only be the root node")
                    void thenThereShouldNoNodes() {
                        assertEquals(1, looseOctree.nodes[givenDepth].length);
                    }

                    @Test
                    @DisplayName("then there should be exactly 1 node per dimension")
                    void thenThereShouldBeExactly1NodePerDimension() {
                        assertNumberOfNodesPerDimension(givenDepth, 1);
                    }

                    @Test
                    @DisplayName("then there should be an array out of bounds when accessing the 2nd element")
                    void thenThereShouldBeAnArrayOutOfBoundsWhenAccessingThe2ndElement() {
                        assertAll(
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][0][1].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][1][0].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][1][0][0].insertObject(null))
                        );
                    }
                }


                @Nested
                @DisplayName("boundingCubeLength tests")
                class BCLengthTests {

                    /**
                     * Only the root node exists, which contains the whole world.
                     */
                    @Test
                    @DisplayName("then boundingCubeLength(0) should return k * worldSize / 1")
                    void thenBoundingCubeLength0ShouldThrowAnException() {
                        assertEquals(k * worldSize, looseOctree.boundingCubeLength(givenDepth));
                    }
                }


                @Nested
                @DisplayName("boundingCubeSpacing tests")
                class BCSpacingTests {

                    /**
                     * At level 0 there only is the imaginary root node. There are no other nodes so spacing to other
                     * nodes makes no sense here.
                     */
                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void thenAnIllegalArgumentExceptionShouldBeThrown() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeSpacing(givenDepth));
                    }
                }
            }


            @Nested
            @DisplayName("given depth of 1")
            class DepthOf1 {

                int givenDepth = 1;


                @Nested
                @DisplayName("accessing nodes array tests")
                class ArrayNodesTests {

                    @Test
                    @DisplayName("then there should be a total of 8 nodes")
                    void thenThereShouldBeATotalOf8Nodes() {
                        assertEquals(8, countTotalNodes(looseOctree.nodes[givenDepth]));
                    }

                    @Test
                    @DisplayName("then there should be exactly 2 nodes per dimension")
                    void thenThereShouldBeExactly2NodesPerDimension() {
                        assertNumberOfNodesPerDimension(givenDepth, 2);
                    }

                    @Test
                    @DisplayName("then there should be an array out of bounds when accessing the 9th element")
                    void thenThereShouldBeAnArrayOutOfBoundsWhenAccessingThe9ThElement() {
                        // call any method on the array as assertThrows needs a void or consumer, not a
                        // concrete value.
                        // index 2 is out of bounds
                        assertAll(
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][0][2].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][2][0].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][2][0][0].insertObject(null))
                        );
                    }
                }


                @Nested
                @DisplayName("boundingCubeLength tests")
                class BCLengthTests {

                    @Test
                    @DisplayName("then boundingCubeLength(1) should return k * worldSize / 2")
                    void thenBoundingCubeLength1ShouldReturnKWorldSize4() {
                        assertEquals(k * worldSize / 2, looseOctree.boundingCubeLength(givenDepth));
                    }
                }


                @Nested
                @DisplayName("boundingCubeSpacing tests")
                class BCSpacingTests {

                    @Test
                    @DisplayName("then boundingCubeSpacing(1) should return worldSize/ 2")
                    void thenBoundingCubeSpacing1ShouldReturnWorldSize2() {
                        assertEquals(worldSize / 2, looseOctree.boundingCubeSpacing(givenDepth));
                    }

                }
            }


            @Nested
            @DisplayName("given depth of 2")
            class DepthOf2 {

                int givenDepth = 2;


                @Nested
                @DisplayName("accessing nodes array tests")
                class ArrayNodesTests {

                    @Test
                    @DisplayName("then there should be a total of 64 nodes")
                    void thenThereShouldBeATotalOf64Nodes() {
                        assertEquals(64, countTotalNodes(looseOctree.nodes[givenDepth]));
                    }

                    @Test
                    @DisplayName("then there should be exactly 4 nodes per dimension")
                    void thenThereShouldBeExactly4NodesPerDimension() {
                        assertNumberOfNodesPerDimension(givenDepth, 4);
                    }

                    @Test
                    @DisplayName("then there should be an array out of bounds when accessing the 65th element")
                    void thenThereShouldBeAnArrayOutOfBoundsWhenAccessingThe65ThElement() {
                        // call any method on the array as assertThrows needs a void or consumer, not a
                        // concrete value.
                        // index 4 is out of bounds
                        assertAll(
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][0][4].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][4][0].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][4][0][0].insertObject(null))
                        );
                    }
                }


                @Nested
                @DisplayName("boundingCubeLength tests")
                class BCLengthTests {

                    @Test
                    @DisplayName("then boundingCubeLength(2) should return k * worldSize / 4")
                    void thenBoundingCubeLength2ShouldReturnKWorldSize8() {
                        assertEquals(k * worldSize / 4, looseOctree.boundingCubeLength(givenDepth));
                    }
                }


                @Nested
                @DisplayName("boundingCubeSpacing tests")
                class BCSpacingTests {

                    @Test
                    @DisplayName("then boundingCubeSpacing(2) should return worldSize / 4")
                    void thenBoundingCubeSpacing2ShouldReturnWorldSize4() {
                        assertEquals(worldSize / 4, looseOctree.boundingCubeSpacing(givenDepth));
                    }
                }
            }


            @Nested
            @DisplayName("given depth of 3")
            class DepthOf3 {

                int givenDepth = 3;


                @Nested
                @DisplayName("accessing nodes array tests")
                class ArrayNodesTests {

                    @Test
                    @DisplayName("then there should be a total of 512 nodes")
                    void thenThereShouldBeATotalOf512Nodes() {
                        assertEquals(512, countTotalNodes(looseOctree.nodes[givenDepth]));
                    }

                    @Test
                    @DisplayName("then there should be exactly 8 nodes per dimension")
                    void thenThereShouldBeExactly8NodesPerDimension() {
                        assertNumberOfNodesPerDimension(givenDepth, 8);
                    }

                    @Test
                    @DisplayName("then there should be an array out of bounds when accessing the 513th element")
                    void thenThereShouldBeAnArrayOutOfBoundsWhenAccessingThe9ThElement() {
                        // call any method on the array as assertThrows needs a void or consumer, not a
                        // concrete value.
                        // index 8 is out of bounds
                        assertAll(
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][0][8].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][0][8][0].insertObject(null)),
                                () -> assertThrows(IndexOutOfBoundsException.class,
                                        () -> looseOctree.nodes[givenDepth][8][0][0].insertObject(null))
                        );
                    }
                }


                @Nested
                @DisplayName("boundingCubeLength tests")
                class BCLengthTests {

                    @Test
                    @DisplayName("then boundingCubeLength(3) should return k * worldSize / 8")
                    void thenBoundingCubeLength3ShouldReturnKWorldSize16() {
                        assertEquals(k * worldSize / 8, looseOctree.boundingCubeLength(givenDepth));
                    }
                }


                @Nested
                @DisplayName("boundingCubeSpacing tests")
                class BCSpacingTests {

                    @Test
                    @DisplayName("then boundingCubeSpacing(3) should return worldSize / 8")
                    void thenBoundingCubeSpacing3ShouldReturnWorldSize8() {
                        assertEquals(worldSize / 8, looseOctree.boundingCubeSpacing(givenDepth));
                    }

                }
            }


            /**
             * Given depth > maxDepth
             */
            @Nested
            @DisplayName("given depth of 4")
            class DepthOf4 {

                int givenDepth = 4;


                @Nested
                @DisplayName("accessing nodes array tests")
                class ArrayNodesTests {

                    @Test
                    @DisplayName("then an IndexOutOfBoundsException should be thrown")
                    void thenAnIndexOutOfBoundsExceptionShouldBeThrown() {
                        // call any method on the array as assertThrows needs a void or consumer, not a
                        // concrete value.
                        assertThrows(IndexOutOfBoundsException.class,
                                () -> looseOctree.nodes[givenDepth][0][0][0].insertObject(null));
                    }
                }


                @Nested
                @DisplayName("boundingCubeLength tests")
                class BCLengthTests {


                    @Test
                    @DisplayName("then boundingCubeLength(4) should throw an Exception")
                    void thenBoundingCubeLength4ShouldThrowAnException() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeLength(givenDepth));
                    }
                }


                @Nested
                @DisplayName("boundingCubeSpacing tests")
                class BCSpacingTests {

                    @Test
                    @DisplayName("then boundingCubeSpacing(4) should throw an IllegalArgumentExceptions")
                    void thenBoundingCubeSpacing4ShouldThrowAnIllegalArgumentExceptions() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeSpacing(givenDepth));
                    }

                }
            }
        }


        @Nested
        @DisplayName("tests based on object radius")
        class RadiusTests {

            /*

            A given level in the octree can accommodate any abject whose radius is less than or equal to 1/4 of the
            bounding cube edge length, regardless of its position. Any object with a radius <= 1/8 of the bounding cube
            edge length should go in the next deeper level in the tree.

            k = 2
            maxDepth = 3

            | depth | bounding cube edge length | maxRadius |   minRadius   |
            |   0   |     16 = worldSize * k    |  Illegal  |    Illegal    |
            |   1   |           8               |   <= 4    |     > 2       |
            |   2   |           4               |   <= 2    |     > 1       |
            |   3   |           2               |   <= 1    |     > 0       |  note: > 0 because there is no next level,
            |   4   |     Illegal Argument      |           |               |   therefore maxDepth puts the object here

             */


            @Nested
            @DisplayName("given object radius of -0.1")
            class RadiusNeg01 {

                double radius = -0.1;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {

                    /**
                     * Objects with a negative radius are incorrectly initialized
                     */
                    @Test
                    @DisplayName("then calcDepth(-0.1) should throw an exception")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        // position doesn't matter because the box has an illegal radius
                        when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then calcIndex(boxMock) should throw an exception")
                    void errorIllegalRadius() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 0")
            class RadiusOf0 {

                double radius = 0;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {

                    /**
                     * Objects with no radius are incorrectly initialized
                     */
                    @Test
                    @DisplayName("then calcDepth(0) should throw an exception")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        // position doesn't matter because the box has an illegal radius
                        when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then calcIndex(boxMock) should throw an exception")
                    void errorIllegalRadius() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 0.1")
            class RadiusOf01 {

                double radius = 0.1;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {

                    /**
                     * maxDepth puts the object in depth 3, normally it would be in depth 6
                     */
                    @Test
                    @DisplayName("then calcDepth(0.1) should return 3")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(3, looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then the calculation should be the same for x, y and z dimension")
                    void sameCalcAllDims() {
                        // three random but equal values from range [-worldSize/2 + radius, worldSize/2 - radius]
                        Point center = new Point(-3.9, -3.9, -3.9);
                        when(boxMock.calcCenter()).thenReturn(center);

                        Point result = looseOctree.calcIndex(boxMock);

                        assertXYAndZAreTheSame(result);
                    }

                    /**
                     * radius = 0.1 => objects are stored at depth 3 (because of maxDepth).  There are 8 indices for
                     * each dimension at level 3, each index is based on an object's position: <br>
                     * <ul>
                     *     <li> object position range (for a single dimension)  :  index</li>
                     *     <li>(-inf, -3.9): Illegal Position</li>
                     *     <li>[-3.9, -3): 0 </li>
                     *     <li>[-3, -2) : 1 </li>
                     *     <li>[-2, -1) : 2</li>
                     *     <li>[-1, 0) : 3 </li>
                     *     <li>[0, 1) : 4</li>
                     *     <li>[1, 2) : 5</li>
                     *     <li>[2, 3) : 6</li>
                     *     <li>[3, 3.9] : 7 </li>
                     *     <li>(3.9, inf): Illegal Position</li>
                     * </ul>
                     */
                    @Nested
                    @DisplayName("position tests")
                    class PositionTest {

                        /**
                         * object's center is the very border of the world, only one half of the object lies in the
                         * world
                         */
                        @Test
                        @DisplayName("then object with center (-4.0, 0, 0) should throw an error")
                        void errorPosTooLow() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-4, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }


                        @Test
                        @DisplayName("then object with center (-3.9, 0, 0) should return (0, 4, 4)")
                        void centerNeg3900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-3.9, 0, 0));
                            assertEquals(new Point(0, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-3.1, 0, 0) should return (0, 4, 4)")
                        void centerNeg3100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-3.1, 0, 0));
                            assertEquals(new Point(0, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-3.0, 0, 0) should return (1, 4, 4)")
                        void centerNeg3000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-3.0, 0, 0));
                            assertEquals(new Point(1, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.1, 0, 0) should return (1, 4, 4)")
                        void centerNeg2100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.1, 0, 0));
                            assertEquals(new Point(1, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.0, 0, 0) should return (2, 4, 4)")
                        void centerNeg2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.0, 0, 0));
                            assertEquals(new Point(2, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-1.1, 0, 0) should return (2, 4, 4)")
                        void centerNeg1100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-1.1, 0, 0));
                            assertEquals(new Point(2, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-1.0, 0, 0) should return (3, 4, 4)")
                        void centerNeg1000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-1.0, 0, 0));
                            assertEquals(new Point(3, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-0.1, 0, 0) should return (3, 4, 4)")
                        void centerNeg0100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-0.1, 0, 0));
                            assertEquals(new Point(3, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0, 0, 0) should return (4, 4, 4)")
                        void center000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                            assertEquals(new Point(4, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0.9, 0, 0) should return (4, 4 ,4)")
                        void center0900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0.9, 0, 0));
                            assertEquals(new Point(4, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (1.0, 0, 0) should return (5, 4, 4)")
                        void center1000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.0, 0, 0));
                            assertEquals(new Point(5, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (1.9, 0, 0) should return (5, 4, 4)")
                        void center1900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.9, 0, 0));
                            assertEquals(new Point(5, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.0, 0, 0) should return (6, 4, 4)")
                        void center2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.0, 0, 0));
                            assertEquals(new Point(6, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.9, 0, 0) should return (6, 4, 4)")
                        void center2900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.9, 0, 0));
                            assertEquals(new Point(6, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (3.0, 0, 0) should return (7, 4, 4)")
                        void center3000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(3.0, 0, 0));
                            assertEquals(new Point(7, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (3.9, 0, 0) should return (7, 4, 4)")
                        void center3900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(3.9, 0, 0));
                            assertEquals(new Point(7, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (4.0, 0, 0) should thrown an exception")
                        void errorCenter4000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(4.0, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 1")
            class RadiusOf1 {

                double radius = 1.0;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(1.0) should return 3")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(3, looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then the calculation should be the same for x, y and z dimension")
                    void sameCalcAllDims() {
                        // three random but equal values from range [-worldSize/2 + radius, worldSize/2 - radius]
                        Point center = new Point(-3, -3, -3);
                        when(boxMock.calcCenter()).thenReturn(center);

                        Point result = looseOctree.calcIndex(boxMock);

                        assertXYAndZAreTheSame(result);
                    }

                    /**
                     * radius = 1.0 => objects are stored at depth 3 (because of maxDepth).  There are 8 indices for
                     * each dimension at level 3, each index is based on an object's position: <br>
                     * <ul>
                     *     <li> object position range (for a single dimension)  :  index</li>
                     *     <li>(-inf, -3.0): Illegal Position</li>
                     *     <li>[-3, -2) : 1 </li>
                     *     <li>[-2, -1) : 2</li>
                     *     <li>[-1, 0) : 3 </li>
                     *     <li>[0, 1) : 4</li>
                     *     <li>[1, 2) : 5</li>
                     *     <li>[2, 3) : 6</li>
                     *     <li>[3.0, 3.0] : 7 </li>
                     *     <li>(3.0, inf): Illegal Position</li>
                     * </ul>
                     */
                    @Nested
                    @DisplayName("position tests")
                    class PositionTest {

                        /**
                         * object's center is the very border of the world, only one half of the object lies in the
                         * world
                         */
                        @Test
                        @DisplayName("then object with center (-3.1, 0, 0) should throw an exception")
                        void centerNeg3100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-3.1, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-3.0, 0, 0) should return (1, 4, 4)")
                        void centerNeg3000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-3.0, 0, 0));
                            assertEquals(new Point(1, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.1, 0, 0) should return (1, 4, 4)")
                        void centerNeg2100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.1, 0, 0));
                            assertEquals(new Point(1, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.0, 0, 0) should return (2, 4, 4)")
                        void centerNeg2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.0, 0, 0));
                            assertEquals(new Point(2, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-1.1, 0, 0) should return (2, 4, 4)")
                        void centerNeg1100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-1.1, 0, 0));
                            assertEquals(new Point(2, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-1.0, 0, 0) should return (3, 4, 4)")
                        void centerNeg1000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-1.0, 0, 0));
                            assertEquals(new Point(3, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-0.1, 0, 0) should return (3, 4, 4)")
                        void centerNeg0100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-0.1, 0, 0));
                            assertEquals(new Point(3, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0, 0, 0) should return (4, 4, 4)")
                        void center000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                            assertEquals(new Point(4, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0.9, 0, 0) should return (4, 4 ,4)")
                        void center0900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0.9, 0, 0));
                            assertEquals(new Point(4, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (1.0, 0, 0) should return (5, 4, 4)")
                        void center1000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.0, 0, 0));
                            assertEquals(new Point(5, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (1.9, 0, 0) should return (5, 4, 4)")
                        void center1900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.9, 0, 0));
                            assertEquals(new Point(5, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.0, 0, 0) should return (6, 4, 4)")
                        void center2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.0, 0, 0));
                            assertEquals(new Point(6, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.9, 0, 0) should return (6, 4, 4)")
                        void center2900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.9, 0, 0));
                            assertEquals(new Point(6, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (3.0, 0, 0) should return (7, 4, 4)")
                        void center3000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(3.0, 0, 0));
                            assertEquals(new Point(7, 4, 4), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (3.1, 0, 0) should throw an exception")
                        void center3100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(3.1, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 1.1")
            class RadiusOf11 {

                double radius = 1.1;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(1.1) should return 2")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(2, looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then the calculation should be the same for x, y and z dimension")
                    void sameCalcAllDims() {
                        // three random but equal values from range [-worldSize/2 + radius, worldSize/2 - radius]
                        Point center = new Point(2.9, 2.9, 2.9);
                        when(boxMock.calcCenter()).thenReturn(center);

                        Point result = looseOctree.calcIndex(boxMock);

                        assertXYAndZAreTheSame(result);
                    }

                    /**
                     * radius = 1.1 => objects are stored at depth 2.  There are 4 indices for
                     * each dimension at level 2, each index is based on an object's position: <br>
                     * <ul>
                     *     <li> object position range (for a single dimension)  :  index</li>
                     *     <li>(-inf, -2.9): Illegal Position</li>
                     *     <li>[-2.9, -2) : 0 </li>
                     *     <li>[-2, -0) : 1</li>
                     *     <li>[0, 2) : 2</li>
                     *     <li>[2, 2.9] : 3</li>
                     *     <li>(2.9, inf): Illegal Position</li>
                     * </ul>
                     */
                    @Nested
                    @DisplayName("position tests")
                    class PositionTest {


                        @Test
                        @DisplayName("then object with center (-3.0, 0, 0) should throw an exception")
                        void centerNeg3000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-3.0, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.9, 0, 0) should return (0, 2, 2)")
                        void centerNeg2900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.9, 0, 0));
                            assertEquals(new Point(0, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.1, 0, 0) should return (0, 2, 2)")
                        void centerNeg2100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.1, 0, 0));
                            assertEquals(new Point(0, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.0, 0, 0) should return (1, 2, 2)")
                        void centerNeg2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.0, 0, 0));
                            assertEquals(new Point(1, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-0.1, 0, 0) should return (1, 2, 2)")
                        void centerNeg0100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-0.1, 0, 0));
                            assertEquals(new Point(1, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0, 0, 0) should return (2, 2, 2)")
                        void center000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                            assertEquals(new Point(2, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (1.9, 0, 0) should return (2, 2 ,2)")
                        void center1900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.9, 0, 0));
                            assertEquals(new Point(2, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.0, 0, 0) should return (3, 2, 2)")
                        void center2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.0, 0, 0));
                            assertEquals(new Point(3, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.9, 0, 0) should return (3, 2, 2)")
                        void center2900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.9, 0, 0));
                            assertEquals(new Point(3, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (3.0, 0, 0) should throw an exception")
                        void center3000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(3.0, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 2")
            class RadiusOf2 {

                double radius = 2;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(2.0) should return 2")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(2, looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then the calculation should be the same for x, y and z dimension")
                    void sameCalcAllDims() {
                        // three random but equal values from range [-worldSize/2 + radius, worldSize/2 - radius]
                        Point center = new Point(2, 2, 2);
                        when(boxMock.calcCenter()).thenReturn(center);

                        Point result = looseOctree.calcIndex(boxMock);

                        assertXYAndZAreTheSame(result);
                    }

                    /**
                     * radius = 2.0 => objects are stored at depth 2.  There are 4 indices for
                     * each dimension at level 2, each index is based on an object's position: <br>
                     * <ul>
                     *     <li> object position range (for a single dimension)  :  index</li>
                     *     <li>(-inf, -2.0): Illegal Position</li>
                     *     <li>[-2, -0) : 1</li>
                     *     <li>[0, 2) : 2</li>
                     *     <li>[2.0, 2.0] : 3</li>
                     *     <li>(2.0, inf): Illegal Position</li>
                     * </ul>
                     */
                    @Nested
                    @DisplayName("position tests")
                    class PositionTest {

                        @Test
                        @DisplayName("then object with center (-2.1, 0, 0) should throw an exception")
                        void centerNeg2100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.1, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-2.0, 0, 0) should return (1, 2, 2)")
                        void centerNeg2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.0, 0, 0));
                            assertEquals(new Point(1, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-0.1, 0, 0) should return (1, 2, 2)")
                        void centerNeg0100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-0.1, 0, 0));
                            assertEquals(new Point(1, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0, 0, 0) should return (2, 2, 2)")
                        void center000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                            assertEquals(new Point(2, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (1.9, 0, 0) should return (2, 2 ,2)")
                        void center1900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.9, 0, 0));
                            assertEquals(new Point(2, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.0, 0, 0) should return (3, 2, 2)")
                        void center2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.0, 0, 0));
                            assertEquals(new Point(3, 2, 2), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.1, 0, 0) should throw an exception")
                        void center2100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.1, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 2.1")
            class RadiusOf21 {

                double radius = 2.1;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(2.1) should return 1")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(1, looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then the calculation should be the same for x, y and z dimension")
                    void sameCalcAllDims() {
                        // three random but equal values from range [-worldSize/2 + radius, worldSize/2 - radius]
                        Point center = new Point(-1.9, -1.9, -1.9);
                        when(boxMock.calcCenter()).thenReturn(center);

                        Point result = looseOctree.calcIndex(boxMock);

                        assertXYAndZAreTheSame(result);
                    }

                    /**
                     * radius = 2.1 => objects are stored at depth 1.  There are 2 indices for
                     * each dimension at level 1, each index is based on an object's position: <br>
                     * <ul>
                     *     <li> object position range (for a single dimension)  :  index</li>
                     *     <li>(-inf, -1.9): Illegal Position</li>
                     *     <li>[-1.9, 0) : 0</li>
                     *     <li>[0, 1.9] : 1</li>
                     *     <li>(1.9, inf): Illegal Position</li>
                     * </ul>
                     */
                    @Nested
                    @DisplayName("position tests")
                    class PositionTest {

                        @Test
                        @DisplayName("then object with center (-2.0, 0, 0) should throw an exception")
                        void centerNeg2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-2.0, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-1.9, 0, 0) should return (0, 1, 1)")
                        void centerNeg1900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-1.9, 0, 0));
                            assertEquals(new Point(0, 1, 1), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (-0.1, 0, 0) should return (0, 1, 1)")
                        void centerNeg0100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-0.1, 0, 0));
                            assertEquals(new Point(0, 1, 1), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0, 0, 0) should return (1, 1, 1)")
                        void center000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                            assertEquals(new Point(1, 1, 1), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (1.9, 0, 0) should return (1, 1 ,1)")
                        void center1900() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.9, 0, 0));
                            assertEquals(new Point(1, 1, 1), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (2.0, 0, 0) should throw an exception")
                        void center2000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(2.0, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 4")
            class RadiusOf4 {

                double radius = 4.0;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {
                    /**
                     * Object has same size as the world. It will be placed in the highest level after the root,
                     * which is depth 1
                     */
                    @Test
                    @DisplayName("then calcDepth(4) should return 1")
                    void thenCalcDepth4ShouldReturn1() {
                        assertEquals(1, looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then the calculation should be the same for x, y and z dimension")
                    void sameCalcAllDims() {
                        // three random but equal values from range [-worldSize/2 + radius, worldSize/2 - radius]
                        Point center = new Point(0, 0, 0);
                        when(boxMock.calcCenter()).thenReturn(center);

                        Point result = looseOctree.calcIndex(boxMock);

                        assertXYAndZAreTheSame(result);
                    }

                    /**
                     * radius = 4.0 => objects are stored at depth 1.  There are 2 indices for
                     * each dimension at level 1, each index is based on an object's position: <br>
                     * <ul>
                     *     <li> object position range (for a single dimension)  :  index</li>
                     *     <li>(-inf, 0): Illegal Position</li>
                     *     <li>[0, 0] : 1</li>
                     *     <li>(0, inf): Illegal Position</li>
                     * </ul>
                     */
                    @Nested
                    @DisplayName("position tests")
                    class PositionTest {

                        @Test
                        @DisplayName("then object with center (-0.1, 0, 0) should throw an exception")
                        void centerNeg0100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(-0.1, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0, 0, 0) should return (1, 1, 1)")
                        void center000() {
                            when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                            assertEquals(new Point(1, 1, 1), looseOctree.calcIndex(boxMock));
                        }

                        @Test
                        @DisplayName("then object with center (0.1, 0, 0) should throw an exception")
                        void center0100() {
                            when(boxMock.calcCenter()).thenReturn(new Point(1.9, 0, 0));
                            assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                        }
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 4.1")
            class RadiusOf41 {

                double radius = 4.1;


                @Nested
                @DisplayName("calcDepth tests")
                class CalcDepthTests {
                    /**
                     * Object would be of size 8.2, which is larger than the world size of 8. The object would not fit
                     * in the world which is a paradox.
                     */
                    @Test
                    @DisplayName("then an IllegalArgumentException should be thrown")
                    void thenAnIllegalArgumentExceptionShouldBeThrown() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcDepth(radius));
                    }
                }


                @Nested
                @DisplayName("calcIndex tests")
                class CalcIndexTests {
                    @Mock
                    AABB boxMock;

                    @BeforeEach
                    void setup() {
                        // position doesn't matter because the box has an illegal radius
                        when(boxMock.calcCenter()).thenReturn(new Point(0, 0, 0));
                        when(boxMock.calcRadius()).thenReturn(radius);
                    }

                    @Test
                    @DisplayName("then calcIndex(boxMock) should throw an exception")
                    void errorIllegalRadius() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcIndex(boxMock));
                    }
                }
            }
        }


        /**
         * Tests for insertObject(). <br>
         * insertObject() heavily depends on calcDepth() and calcIndex(), which are both extensively tested. Therefore,
         * insertObject() doesn't need to be as intensely tested as the other two methods.
         */
        @Nested
        @DisplayName("insertion tests")
        class InsertionTests {

            /**
             * Concrete implementation of the abstract {@linkplain AABB} class
             */
            class ConcreteAABB extends AABB {
                ConcreteAABB(Point vertA, Point vertB) {
                    super(vertA, vertB);
                }
            }


            AABB box;


            @Nested
            @DisplayName("One AABB")
            class OneAABBTests {

                HashSet<AABB> nodeContent = new HashSet<>();

                @Test
                @DisplayName("AABB((1, 2, 3), (3, 2, 1)) should be inserted at [3][6][6][6]")
                void box123321() {
                    // center at (2, 2, 2), dimensions: 2x2x2, radius: 1.0
                    Point pointA = new Point(1, 1.5, 1);
                    Point pointB = new Point(3, 2.5, 3);

                    box = new ConcreteAABB(pointA, pointB);
                    nodeContent.add(box);

                    // radius 1.0  => depth = 3
                    // center (2,2,2) => indices x: 6, y: 6, z: 6
                    addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[3][6][6][6], nodeContent);
                }

                /**
                 * The object is not fully enclosed in the world
                 */
                @Test
                @DisplayName("AABB((1, 2, 3)(4, 5, 6)) should throw an exception")
                void box123456() {
                    // center at (2.5, 3.5, 4.5), dimensions: 3x3x3, radius: 1.5
                    Point pointA = new Point(1, 2, 3);
                    Point pointB = new Point(4, 5, 6);

                    box = new ConcreteAABB(pointA, pointB);

                    // radius 1.5  => depth = 2
                    // center (2.5, 3.5, 4.5) => indices x: 6, y: error, z: error
                    assertThrows(IllegalArgumentException.class, () -> looseOctree.insertObject(box));
                }

                @Test
                @DisplayName("AABB((2, -2, 1), (-1, 3, -3)) should be inserted at [1][1][1][0]")
                void box2Neg21Neg13Neg3() {
                    // center at (0.5, 0.5, -1), dimensions: 3x5x4, radius: 2.5
                    Point pointA = new Point(-1, -2, -3);
                    Point pointB = new Point(2, 3, 1);

                    box = new ConcreteAABB(pointA, pointB);
                    nodeContent.add(box);

                    // radius 2.5  => depth = 1
                    // center (0.5, 0.5, -1) => indices x: 1, y: 1, z: 0
                    addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[1][1][1][0], nodeContent);
                }

                @Test
                @DisplayName("AABB((-2.3, 2.5, 1.3), (1.0, -1.2, -0.66)) should be inserted at [2][1][2][2]")
                void boxNeg23251310Neg12Neg066() {
                    // center at (-0.65, 0.6, 0.32), dimensions: 3.3 x 3.7 x 1.96, radius: 1.85
                    Point pointA = new Point(-2.3, -1.2, -0.66);
                    Point pointB = new Point(1.0, 2.5, 1.3);

                    box = new ConcreteAABB(pointA, pointB);
                    nodeContent.add(box);

                    // radius 1.85  => depth = 2
                    // center (-0.65, 0.6, 0.32) => indices x: 1, y: 2, z: 2
                    addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[2][1][2][2], nodeContent);
                }

                @Test
                @DisplayName("AABB(1.65,-3.4,-0.7)(2.99,3.0,0.00000001) should throw an exception")
                void box165Neg34Neg072993000000001() {
                    // center at (2.32, 0.2, -0.35), dimensions: 4.64 x 6.4 x 0.69999999, radius: 3.2
                    Point pointA = new Point(1.65, -3.4, -0.7);
                    Point pointB = new Point(2.99, 3.0, 0.00000001);

                    box = new ConcreteAABB(pointA, pointB);

                    // radius 3.2  => depth = 1
                    // center (2.32, 0.2, -0.35) => indices x: error, y: 1, z: 0
                    assertThrows(IllegalArgumentException.class, () -> looseOctree.insertObject(box));
                }
            }


            @Nested
            @DisplayName("Two Boxes")
            class TwoBoxesTests {

                AABB box2;
                HashSet<AABB> contentNode1 = new HashSet<>();
                HashSet<AABB> contentNode2 = new HashSet<>();

                @Test
                @DisplayName("Insert two boxes in the same node")
                void twoBoxesOneNode() {
                    // Hint: The boxes do not touch
                    // center at (1.0, 0.6, 0.32), dimensions: 1.0 x 1.0 x 1.0, radius: 0.5
                    Point pointA1 = new Point(0.5, 1.5, 0.3);
                    Point pointB1 = new Point(1.5, 2.5, 1.3);

                    // center at (1.98, 0.6, 0.32), dimensions: 1.0 x 1.0 x 1.0, radius: 0.5
                    Point pointA2 = new Point(1.51, 1.5, 0.3);
                    Point pointB2 = new Point(2.45, 2.5, 1.3);

                    // radius 0.5 => depth = 3
                    // center (1.0, 2.0, 0.8) => indices x: 5, y: 6, z: 4
                    box = new ConcreteAABB(pointA1, pointB1);
                    // radius 0.5  => depth = 3
                    // center (1.98, 2.0, 0.8) => indices x: 5, y: 6, z: 4
                    box2 = new ConcreteAABB(pointA2, pointB2);

                    contentNode1.add(box);
                    contentNode2.add(box);
                    contentNode2.add(box2);

                    assertAll(
                            () -> addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[3][5][6][4], contentNode1),
                            () -> addBoxAndAssertBoxAddedToOctree(box2, looseOctree.nodes[3][5][6][4], contentNode2)
                    );
                }

                @Test
                @DisplayName("Insert two boxes which have the very same position")
                void twoBoxesSamePosition() {
                    // center at (-0.65, 0.6, 0.32), dimensions: 3.3 x 3.7 x 1.96, radius: 1.85
                    Point pointA1 = new Point(-2.3, -1.2, -0.66);
                    Point pointB1 = new Point(1.0, 2.5, 1.3);

                    // center at (-0.65, 0.6, 0.32), dimensions: 3.3 x 3.7 x 1.96, radius: 1.85
                    Point pointA2 = new Point(-2.3, -1.2, -0.66);
                    Point pointB2 = new Point(1.0, 2.5, 1.3);

                    box = new ConcreteAABB(pointA1, pointB1);
                    box2 = new ConcreteAABB(pointA2, pointB2);

                    contentNode1.add(box);
                    contentNode2.add(box);
                    contentNode2.add(box2);

                    // radius 1.85  => depth = 2
                    // center (-0.65, 0.6, 0.32) => indices x: 1, y: 2, z: 2
                    assertAll(
                            () -> addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[2][1][2][2], contentNode1),
                            () -> addBoxAndAssertBoxAddedToOctree(box2, looseOctree.nodes[2][1][2][2], contentNode2)
                    );
                }

                @Test
                @DisplayName("Insert two boxes, same depth, each at a different node")
                void twoBoxesSameDepthDifferentNodes() {
                    // center at (0.14485, 2.6, -2.0811), dimensions: 0.0237 x 2.2 x 0.2822, radius: 1.1
                    Point pointA1 = new Point(0.133, 1.5, -2.2222);
                    Point pointB1 = new Point(0.1567, 3.7, -1.94);

                    // center at (2.0583, 1.7005, 0.16), dimensions: 0.7834 x 3.399 x 0.32, radius: 1.6995
                    Point pointA2 = new Point(1.6666, 0.001, 0.98);
                    Point pointB2 = new Point(2.45, 3.4, 1.3);

                    // radius 1.1 => depth = 2
                    // center (0.14485, 2.6, -2.0811) => indices x: 2, y: 3, z: 0
                    box = new ConcreteAABB(pointA1, pointB1);
                    // radius 1.6995  => depth = 2
                    // center (2.0583, 1.7005, 0.16) => indices x: 3, y: 2, z: 2
                    box2 = new ConcreteAABB(pointA2, pointB2);

                    contentNode1.add(box);
                    contentNode2.add(box2);

                    assertAll(
                            () -> addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[2][2][3][0], contentNode1),
                            () -> addBoxAndAssertBoxAddedToOctree(box2, looseOctree.nodes[2][3][2][2], contentNode2)
                    );
                }

                @Test
                @DisplayName("Insert two boxes, different depth, same node indices")
                void twoBoxesDiffDepthSameNodeIndices() {
                    // center at (-2.5, -0.5, 0.5), dimensions: 3.0 x 3.0 x 3.0, radius: 1.5
                    Point pointA1 = new Point(-4.0, -2.0, -1.0);
                    Point pointB1 = new Point(-1.0, 1.0, 2.0);

                    // center at (-3.5, -2.5, -1.5), dimensions: 1.0 x 1.0 x 1.0, radius: 0.5
                    Point pointA2 = new Point(-4.0, -3.0, -2.0);
                    Point pointB2 = new Point(-3.0, -2.0, -1.0);

                    // radius 1.5 => depth = 2
                    // center (-2.5, -0.5, 0.5) => indices x: 0, y: 1, z: 2
                    box = new ConcreteAABB(pointA1, pointB1);
                    // radius 0.5  => depth = 3
                    // center (-3.5, -2.5, -1.5) => indices x: 0, y: 1, z: 2
                    box2 = new ConcreteAABB(pointA2, pointB2);

                    contentNode1.add(box);
                    contentNode2.add(box2);

                    assertAll(
                            () -> addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[2][0][1][2], contentNode1),
                            () -> addBoxAndAssertBoxAddedToOctree(box2, looseOctree.nodes[3][0][1][2], contentNode2)
                    );
                }

                @Test
                @DisplayName("insert two boxes, different depths, each at a different node")
                void twoBoxesDiffDepthsDiffNodes() {
                    // center at (-2.5, -0.5, 0.5), dimensions: 3.0 x 3.0 x 3.0, radius: 1.5
                    Point pointA1 = new Point(-4.0, -2.0, -1.0);
                    Point pointB1 = new Point(-1.0, 1.0, 2.0);

                    // center at (3.5, 2.5, 1.5), dimensions: 1.0 x 1.0 x 1.0, radius: 0.5
                    Point pointA2 = new Point(3.0, 2.0, 1.0);
                    Point pointB2 = new Point(4.0, 3.0, 2.0);

                    // radius 1.5 => depth = 2
                    // center (-2.5, -0.5, 0.5) => indices x: 0, y: 1, z: 2
                    box = new ConcreteAABB(pointA1, pointB1);
                    // radius 0.5  => depth = 3
                    // center (3.5, 2.5, 1.5) => indices x: 7 y: 6, z: 5
                    box2 = new ConcreteAABB(pointA2, pointB2);

                    contentNode1.add(box);
                    contentNode2.add(box2);

                    assertAll(
                            () -> addBoxAndAssertBoxAddedToOctree(box, looseOctree.nodes[2][0][1][2], contentNode1),
                            () -> addBoxAndAssertBoxAddedToOctree(box2, looseOctree.nodes[3][7][6][5], contentNode2)
                    );
                }
            }
        }
    }
}