package org.example.SequenceFinder.Octree;

import org.example.SequenceFinder.GeometricObjects.Box;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class uses Behavior Driven testing.
 */
@ExtendWith(MockitoExtension.class)
public class LooseOctreeTest {

    /**
     * looseness value of the loose Octree. Some formulas need k=2, otherwise the mathematical transformation wouldn't
     * work and these formulas would be different.<br>
     * According to Thatcher Ulrich in "Game Programming Gems (2000), Loose Octrees" a value of k=2 is a good balance
     * between loose but not too loose.
     */
    double k = 2;


    @Nested
    @DisplayName("given a LooseOctree with maxDepth = 3 and worldSize = 8")
    class givenALooseOctreeWithMaxDepth3AndWorldSize8 {

        LooseOctree<Box> looseOctree;

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
        int countTotalNodes(Box[][][] nodes) {
            int counter = 0;
            for (Box[][] x : nodes) {
                for (Box[] y : x) {
                    for (Box z : y) {
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
        boolean equallySizedDimensions(Box[][][] nodes) {
            // all dimensions must be equally sized, doesn't matter which one is chosen to be compared against.
            // here the x dimension is chosen.
            int referenceDimension = nodes.length;

            for (Box[][] y : nodes) {
                if (y.length != referenceDimension) {
                    return false;
                }
                for (Box[] z : y) {
                    if (z.length != referenceDimension) {
                        return false;
                    }
                }
            }
            return true;
        }

        //TODO: add tests for depth < 0


        @Nested
        @DisplayName("given depth of 0")
        class givenDepthOf0 {

            int givenDepth = 0;


            @Nested
            @DisplayName("accessing nodes array tests")
            class accessingNodesArrayTests {


                /**
                 * The root node is imaginary. There could only be an object in the root if its radius was larger than
                 * worldSize / 2, which would mean the object is bigger than the entire world, which is a paradox.
                 */
                @Test
                @DisplayName("then there should be no nodes")
                void thenThereShouldNoNodes() {
                    assertEquals(0, looseOctree.nodes[givenDepth].length);
                }

                @Test
                @DisplayName("then an IndexOutOfBoundsException should be thrown")
                void thenAnIndexOutOfBoundsExceptionShouldBeThrown() {
                    // call any method on the array as assertThrows needs a void or consumer, not a
                    // concrete value.
                    // index 1 is out of bounds
                    assertAll(
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][0][1].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][1][0].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][1][0][0].calcCenter())
                    );
                }
            }


            @Nested
            @DisplayName("boundingCubeLength tests")
            class boundingCubeLengthTests {

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
            class boundingCubeSpacingTests {

                /**
                 * At level 0 there only is the imaginary root node. There are no other nodes so spacing to other nodes
                 * makes no sense here.
                 */
                @Test
                @DisplayName("then an IllegalArgumentException should be thrown")
                void thenAnIllegalArgumentExceptionShouldBeThrown() {
                    assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeSpacing(givenDepth));
                }

            }


            @Nested
            @DisplayName("calcDepth tests")
            class calcDepthTests {

            }


            @Nested
            @DisplayName("calcIndex tests")
            class calcIndexTests {

            }


            @Nested
            @DisplayName("insertObject tests")
            class insertObjectTests {

            }
        }


        @Nested
        @DisplayName("given depth of 1")
        class givenDepthOf1 {

            int givenDepth = 1;


            @Nested
            @DisplayName("accessing nodes array tests")
            class accessingNodesArrayTests {

                @Test
                @DisplayName("then there should be a total of 8 nodes")
                void thenThereShouldBeATotalOf8Nodes() {
                    assertEquals(8, countTotalNodes(looseOctree.nodes[givenDepth]));
                }

                @Test
                @DisplayName("then there should be exactly 2 nodes per dimension")
                void thenThereShouldBeExactly2NodesPerDimension() {
                    assertAll(
                            // check x dimension
                            () -> assertEquals(2, looseOctree.nodes[givenDepth].length),
                            // check y dimension
                            () -> assertEquals(2, looseOctree.nodes[givenDepth][0].length),
                            // check z dimension
                            () -> assertEquals(2, looseOctree.nodes[givenDepth][0][0].length),

                            // check all dimensions are equally sized.
                            () -> assertTrue(equallySizedDimensions(looseOctree.nodes[givenDepth]))
                    );
                }

                @Test
                @DisplayName("then there should be an array out of bounds when accessing the 9th element")
                void thenThereShouldBeAnArrayOutOfBoundsWhenAccessingThe9ThElement() {
                    // call any method on the array as assertThrows needs a void or consumer, not a
                    // concrete value.
                    // index 2 is out of bounds
                    assertAll(
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][0][2].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][2][0].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][2][0][0].calcCenter())
                    );
                }
            }


            @Nested
            @DisplayName("boundingCubeLength tests")
            class boundingCubeLengthTests {

                @Test
                @DisplayName("then boundingCubeLength(1) should return k * worldSize / 2")
                void thenBoundingCubeLength1ShouldReturnKWorldSize4() {
                    assertEquals(k * worldSize / 2, looseOctree.boundingCubeLength(givenDepth));
                }
            }


            @Nested
            @DisplayName("boundingCubeSpacing tests")
            class boundingCubeSpacingTests {

                @Test
                @DisplayName("then boundingCubeSpacing(1) should return worldSize/ 2")
                void thenBoundingCubeSpacing1ShouldReturnWorldSize2() {
                    assertEquals(worldSize / 2, looseOctree.boundingCubeSpacing(givenDepth));
                }

            }


            @Nested
            @DisplayName("calcDepth tests")
            class calcDepthTests {

            }


            @Nested
            @DisplayName("calcIndex tests")
            class calcIndexTests {

            }


            @Nested
            @DisplayName("insertObject tests")
            class insertObjectTests {

            }
        }


        @Nested
        @DisplayName("given depth of 2")
        class givenDepthOf2 {

            int givenDepth = 2;


            @Nested
            @DisplayName("accessing nodes array tests")
            class accessingNodesArrayTests {

                @Test
                @DisplayName("then there should be a total of 64 nodes")
                void thenThereShouldBeATotalOf64Nodes() {
                    assertEquals(64, countTotalNodes(looseOctree.nodes[givenDepth]));
                }

                @Test
                @DisplayName("then there should be exactly 4 nodes per dimension")
                void thenThereShouldBeExactly4NodesPerDimension() {
                    assertAll(
                            // check x dimension
                            () -> assertEquals(4, looseOctree.nodes[givenDepth].length),
                            // check y dimension
                            () -> assertEquals(4, looseOctree.nodes[givenDepth][0].length),
                            // check z dimension
                            () -> assertEquals(4, looseOctree.nodes[givenDepth][0][0].length),

                            // check all dimensions are equally sized.
                            () -> assertTrue(equallySizedDimensions(looseOctree.nodes[givenDepth]))
                    );
                }

                @Test
                @DisplayName("then there should be an array out of bounds when accessing the 65th element")
                void thenThereShouldBeAnArrayOutOfBoundsWhenAccessingThe65ThElement() {
                    // call any method on the array as assertThrows needs a void or consumer, not a
                    // concrete value.
                    // index 4 is out of bounds
                    assertAll(
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][0][4].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][4][0].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][4][0][0].calcCenter())
                    );
                }
            }


            @Nested
            @DisplayName("boundingCubeLength tests")
            class boundingCubeLengthTests {

                @Test
                @DisplayName("then boundingCubeLength(2) should return k * worldSize / 4")
                void thenBoundingCubeLength2ShouldReturnKWorldSize8() {
                    assertEquals(k * worldSize / 4, looseOctree.boundingCubeLength(givenDepth));
                }
            }


            @Nested
            @DisplayName("boundingCubeSpacing tests")
            class boundingCubeSpacingTests {

                @Test
                @DisplayName("then boundingCubeSpacing(2) should return worldSize / 4")
                void thenBoundingCubeSpacing2ShouldReturnWorldSize4() {
                    assertEquals(worldSize / 4, looseOctree.boundingCubeSpacing(givenDepth));
                }

            }


            @Nested
            @DisplayName("calcDepth tests")
            class calcDepthTests {

            }


            @Nested
            @DisplayName("calcIndex tests")
            class calcIndexTests {

            }


            @Nested
            @DisplayName("insertObject tests")
            class insertObjectTests {

            }
        }


        @Nested
        @DisplayName("given depth of 3")
        class givenDepthOf3 {

            int givenDepth = 3;


            @Nested
            @DisplayName("accessing nodes array tests")
            class accessingNodesArrayTests {

                @Test
                @DisplayName("then there should be a total of 512 nodes")
                void thenThereShouldBeATotalOf512Nodes() {
                    assertEquals(512, countTotalNodes(looseOctree.nodes[givenDepth]));
                }

                @Test
                @DisplayName("then there should be exactly 8 nodes per dimension")
                void thenThereShouldBeExactly8NodesPerDimension() {
                    assertAll(
                            // check x dimension
                            () -> assertEquals(8, looseOctree.nodes[givenDepth].length),
                            // check y dimension
                            () -> assertEquals(8, looseOctree.nodes[givenDepth][0].length),
                            // check z dimension
                            () -> assertEquals(8, looseOctree.nodes[givenDepth][0][0].length),

                            // check all dimensions are equally sized.
                            () -> assertTrue(equallySizedDimensions(looseOctree.nodes[givenDepth]))
                    );
                }

                @Test
                @DisplayName("then there should be an array out of bounds when accessing the 513th element")
                void thenThereShouldBeAnArrayOutOfBoundsWhenAccessingThe9ThElement() {
                    // call any method on the array as assertThrows needs a void or consumer, not a
                    // concrete value.
                    // index 8 is out of bounds
                    assertAll(
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][0][8].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][0][8][0].calcCenter()),
                            () -> assertThrows(IndexOutOfBoundsException.class,
                                    () -> looseOctree.nodes[givenDepth][8][0][0].calcCenter())
                    );
                }
            }


            @Nested
            @DisplayName("boundingCubeLength tests")
            class boundingCubeLengthTests {

                @Test
                @DisplayName("then boundingCubeLength(3) should return k * worldSize / 8")
                void thenBoundingCubeLength3ShouldReturnKWorldSize16() {
                    assertEquals(k * worldSize / 8, looseOctree.boundingCubeLength(givenDepth));
                }
            }


            @Nested
            @DisplayName("boundingCubeSpacing tests")
            class boundingCubeSpacingTests {

                @Test
                @DisplayName("then boundingCubeSpacing(3) should return worldSize / 8")
                void thenBoundingCubeSpacing3ShouldReturnWorldSize8() {
                    assertEquals(worldSize / 8, looseOctree.boundingCubeSpacing(givenDepth));
                }

            }


            @Nested
            @DisplayName("calcDepth tests")
            class calcDepthTests {

            }


            @Nested
            @DisplayName("calcIndex tests")
            class calcIndexTests {

            }


            @Nested
            @DisplayName("insertObject tests")
            class insertObjectTests {

            }
        }


        /**
         * Given depth > maxDepth
         */
        @Nested
        @DisplayName("given depth of 4")
        class givenDepthOf4 {

            int givenDepth = 4;


            @Nested
            @DisplayName("accessing nodes array tests")
            class accessingNodesArrayTests {

                @Test
                @DisplayName("then an IndexOutOfBoundsException should be thrown")
                void thenAnIndexOutOfBoundsExceptionShouldBeThrown() {
                    // call any method on the array as assertThrows needs a void or consumer, not a
                    // concrete value.
                    assertThrows(IndexOutOfBoundsException.class,
                            () -> looseOctree.nodes[givenDepth][0][0][0].calcCenter());
                }
            }


            @Nested
            @DisplayName("boundingCubeLength tests")
            class boundingCubeLengthTests {


                @Test
                @DisplayName("then boundingCubeLength(4) should throw an Exception")
                void thenBoundingCubeLength4ShouldThrowAnException() {
                    assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeLength(givenDepth));
                }
            }


            @Nested
            @DisplayName("boundingCubeSpacing tests")
            class boundingCubeSpacingTests {

                @Test
                @DisplayName("then boundingCubeSpacing(4) should throw an IllegalArgumentExceptions")
                void thenBoundingCubeSpacing4ShouldThrowAnIllegalArgumentExceptions() {
                    assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeSpacing(givenDepth));
                }

            }


            @Nested
            @DisplayName("calcDepth tests")
            class calcDepthTests {

            }


            @Nested
            @DisplayName("calcIndex tests")
            class calcIndexTests {

            }


            @Nested
            @DisplayName("insertObject tests")
            class insertObjectTests {

            }
        }


        @Nested
        @DisplayName("tests based on object radius")
        class testsBasedOnObjectRadius {

            /*

            A given level in the octree can accommodate any abject whose radius is less than or equal to 1/4 of the
            bounding cube edge length, regardless of its position. Any object with a radius <= 1/8 of the bounding cube
            edge length should go in the next deeper level in the tree.

            k = 2

            | depth | bounding cube edge length | maxRadius |   minRadius   |
            |   0   |     16 = worldSize * k    |  Illegal  |    Illegal    |
            |   1   |           8               |   <= 4    |     > 2       |
            |   2   |           4               |   <= 2    |     > 1       |
            |   3   |           2               |   <= 1    |     > 0       |  note: > 0 because there is no next level,
            |   4   |     Illegal Argument      |           |               |   therefore maxDepth puts the object here

             */


            @Nested
            @DisplayName("given object radius of -0.1")
            class givenObjectRadiusOfNeg01 {

                double radius = -0.1;


                @Nested
                @DisplayName("calcDepth tests")
                class calcDepthTests {

                    /**
                     * Objects with a negative radius are incorrectly initialized
                     */
                    @Test
                    @DisplayName("then calcDepth(-0.1) should throw an exception")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcDepth(radius));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 0")
            class givenObjectRadiusOf0 {

                double radius = 0;


                @Nested
                @DisplayName("calcDepth tests")
                class calcDepthTests {

                    /**
                     * Objects with no radius are incorrectly initialized
                     */
                    @Test
                    @DisplayName("then calcDepth(0) should throw an exception")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertThrows(IllegalArgumentException.class, () -> looseOctree.calcDepth(radius));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 0.1")
            class givenObjectRadiusOf01 {

                double radius = 0.1;


                @Nested
                @DisplayName("calcDepth tests")
                class calcDepthTests {

                    /**
                     * maxDepth puts the object in depth 3, normally it would be in depth 6
                     */
                    @Test
                    @DisplayName("then calcDepth(0.1) should return 3")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(3, looseOctree.calcDepth(radius));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 1")
            class givenObjectRadiusOf1 {

                double radius = 1.0;


                @Nested
                @DisplayName("calcDepth tests")
                class calcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(1.0) should return 3")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(3, looseOctree.calcDepth(radius));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 1.1")
            class givenObjectRadiusOf11 {

                double radius = 1.1;


                @Nested
                @DisplayName("calcDepth tests")
                class calcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(1.1) should return 2")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(2, looseOctree.calcDepth(radius));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 2")
            class givenObjectRadiusOf2 {

                double radius = 2;


                @Nested
                @DisplayName("calcDepth tests")
                class calcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(2.0) should return 2")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(2, looseOctree.calcDepth(radius));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 2.1")
            class givenObjectRadiusOf3 {

                double radius = 2.1;


                @Nested
                @DisplayName("calcDepth tests")
                class calcDepthTests {

                    @Test
                    @DisplayName("then calcDepth(2.1) should return 1")
                    void thenCalcDepth0ShouldThrowAnException() {
                        assertEquals(1, looseOctree.calcDepth(radius));
                    }
                }
            }


            @Nested
            @DisplayName("given object radius of 4")
            class givenObjectRadiusOf4 {

                double radius = 4.0;

                /**
                 * Object has same size as the world. It will be paced in the highest after the root, which is depth 1
                 */
                @Test
                @DisplayName("then calcDepth(4) should return 1")
                void thenCalcDepth4ShouldReturn1() {
                    assertEquals(1, looseOctree.calcDepth(radius));
                }
            }


            @Nested
            @DisplayName("given object radius of 4.1")
            class givenObjectRadiusOf41 {

                double radius = 4.1;

                /**
                 * Object would be of size 8.2, which is larger than the world size of 8. The object would not fit in the
                 * world which is a paradox.
                 */
                @Test
                @DisplayName("then an IllegalArgumentException should be thrown")
                void thenAnIllegalArgumentExceptionShouldBeThrown() {
                    assertThrows(IllegalArgumentException.class, () -> looseOctree.calcDepth(radius));
                }
            }
        }


    }

}