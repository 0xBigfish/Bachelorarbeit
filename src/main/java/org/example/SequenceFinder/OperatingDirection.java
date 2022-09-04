package org.example.SequenceFinder;

/**
 * Enum to describe the direction from which items can be placed or removed
 */
public enum OperatingDirection {
    /**
     * FRONT corresponds to an access vector of (0,1,0)
     */
    FRONT,
    /**
     * BACK corresponds to an access vector of (0,-1,0)
     */
    BACK,
    /**
     * LEFT corresponds to an access vector of (1,0,0)
     */
    LEFT,
    /**
     * RIGHT corresponds to an access vector of (-1,0,0)
     */
    RIGHT,
    /**
     * TOP corresponds to an access vector of (0,0,-1)
     */
    TOP
}
