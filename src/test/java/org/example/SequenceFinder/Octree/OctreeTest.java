package org.example.SequenceFinder.Octree;

import org.example.SequenceFinder.GeometricObjects.Box;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OctreeTest{

    /**
     * looseness value of the octree. Some formulas need k=2, otherwise the mathematical tranformation wouldn't work and
     * these formulas would not be the same. <br>
     * According to Ulrich Thatcher in "Game Programming Gems (2000), Loose Octrees" a value of k=2 is a good balance
     * between loose but not too loose.
     */
    double k = 2;


    @Nested
    @DisplayName("given an Octree with maxDepth = 3 and worldSize = 8")
    class givenAnOctreeWithMaxDepth3AndWorldSize8 {

        Octree<Box> octree;

        int worldSize = 8;
        int maxDepth = 3;

        @BeforeEach
        void setup() {
            octree = new Octree<>(maxDepth, worldSize);
        }

        @Nested
        @DisplayName("boundingCubeLength tests")
        class boundingCubeLengthTests {

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