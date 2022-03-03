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

        @Nested
        @DisplayName("boundingCubeLength tests")
        class boundingCubeLengthTests {

            /**
             * Only the root node exists, which contains the whole world.
             */
            @Test
            @DisplayName("then boundingCubeLength(0) should return k * worldSize / 1")
            void thenBoundingCubeLength0ShouldThrowAnException() {
                assertEquals(k * worldSize, looseOctree.boundingCubeLength(0));
            }

            @Test
            @DisplayName("then boundingCubeLength(1) should return k * worldSize / 2")
            void thenBoundingCubeLength1ShouldReturnKWorldSize4() {
                assertEquals(k * worldSize / 2, looseOctree.boundingCubeLength(1));
            }

            @Test
            @DisplayName("then boundingCubeLength(2) should return k * worldSize / 4")
            void thenBoundingCubeLength2ShouldReturnKWorldSize8() {
                assertEquals(k * worldSize / 4, looseOctree.boundingCubeLength(2));
            }

            @Test
            @DisplayName("then boundingCubeLength(3) should return k * worldSize / 8")
            void thenBoundingCubeLength3ShouldReturnKWorldSize16() {
                assertEquals(k * worldSize / 8, looseOctree.boundingCubeLength(3));
            }

            @Test
            @DisplayName("then boundingCubeLength(4) should throw an Exception")
            void thenBoundingCubeLength4ShouldThrowAnException() {
                assertThrows(IllegalArgumentException.class, () -> looseOctree.boundingCubeLength(4));
            }
        }


        @Nested
        @DisplayName("accessing nodes array tests")
        class accessingNodesArrayTests {

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


            @Nested
            @DisplayName("given depth of 0")
            class givenDepthOf0 {

                int givenDepth = 0;

                /**
                 * The root node is imaginary. There could only be an object in the root if its radius was larger than
                 * worldSize / 2, which would mean the object is bigger than the entire world, which is a paradoxon.
                 */
                @Test
                @DisplayName("then there should be no nodes")
                void thenThereShouldBeExactlyOnNodeRootNode() {
                    assertEquals(0, looseOctree.nodes[givenDepth].length);
                }

                @Test
                @DisplayName("then an IndexOutOfBoundsException should be thrown")
                void thenAnIndexOutOfBoundsExceptionShouldBeThrown() {
                    // call any method on the array as asserThrows needs a void or consumer, not a
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
            @DisplayName("given depth of 1")
            class givenDepthOf1 {

                int givenDepth = 1;

                @Test
                @DisplayName("then there should be a total of 8 nodes")
                void thenThereShouldBeAtotalOf8Nodes() {
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
                    // call any method on the array as asserThrows needs a void or consumer, not a
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
            @DisplayName("given depth of 2")
            class givenDepthOf2 {

                int givenDepth = 2;

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
                    // call any method on the array as asserThrows needs a void or consumer, not a
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
            @DisplayName("given depth of 3")
            class givenDepthOf3 {

                int givenDepth = 3;

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
                    // call any method on the array as asserThrows needs a void or consumer, not a
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


            /**
             * Given depth > maxDepth
             */
            @Nested
            @DisplayName("given depth of 4")
            class givenDepthOf4 {

                int givenDepth = 4;

                @Test
                @DisplayName("then an IndexOutOfBoundsException should be thrown")
                void thenAnIndexOutOfBoundsExceptionShouldBeThrown() {
                    // call any method on the array as asserThrows needs a void or consumer, not a
                    // concrete value.
                    assertThrows(IndexOutOfBoundsException.class,
                            () -> looseOctree.nodes[givenDepth][0][0][0].calcCenter());
                }
            }
        }


        @Nested
        @DisplayName("boundingCubeSpacing tests")
        class boundingCubeSpacingTests {

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

}