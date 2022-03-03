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