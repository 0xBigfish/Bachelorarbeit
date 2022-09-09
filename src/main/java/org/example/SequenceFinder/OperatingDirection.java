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
    TOP,
    /**
     * BOTTOM corresponds to an access vector of (0,0,1)
     */
    BOTTOM;

    /**
     * Returns the opposite direction of the current direction
     *
     * @return the opposite direction of the current direction
     */
    public OperatingDirection getOpposite() {
        switch (this) {
            case FRONT:
                return BACK;
            case BACK:
                return FRONT;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    /**
     * Returns the direction to the left of the current direction
     *
     * @return the direction to the left of the current direction
     */
    public OperatingDirection getLeft() {
        switch (this) {
            case FRONT:
            case TOP:
            case BOTTOM:
                return LEFT;
            case BACK:
                return RIGHT;
            case LEFT:
                return BACK;
            case RIGHT:
                return FRONT;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    /**
     * Returns the direction to the right of the current direction
     *
     * @return the direction to the right of the current direction
     */
    public OperatingDirection getRight() {
        switch (this) {
            case FRONT:
            case TOP:
            case BOTTOM:
                return RIGHT;
            case BACK:
                return LEFT;
            case LEFT:
                return FRONT;
            case RIGHT:
                return BACK;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    /**
     * Returns the direction above the current direction
     *
     * @return the direction above the current direction
     */
    public OperatingDirection getTop() {
        switch (this) {
            case FRONT:
            case BACK:
            case LEFT:
            case RIGHT:
                return TOP;
            case TOP:
                return BACK;
            case BOTTOM:
                return FRONT;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    /**
     * Returns the direction below the current direction
     *
     * @return the direction below the current direction
     */
    public OperatingDirection getBottom() {
        switch (this) {
            case FRONT:
            case BACK:
            case LEFT:
            case RIGHT:
                return BOTTOM;
            case TOP:
                return FRONT;
            case BOTTOM:
                return BACK;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
