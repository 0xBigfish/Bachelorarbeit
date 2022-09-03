package org.example.SequenceFinder.Model.Graph;

/**
 * An exception that is thrown, when the state of a {@linkplain Graph} is illegal (i.e. when a node is
 * added to a graph, that already exists, or if an edge, that never existed, is removed from the graph)
 */
public class IllegalGraphStateException extends IllegalArgumentException {

    public IllegalGraphStateException(String message) {
        super(message);
    }

    public IllegalGraphStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
