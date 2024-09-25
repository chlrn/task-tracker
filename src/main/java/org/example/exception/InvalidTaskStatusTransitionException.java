package org.example.exception;

public class InvalidTaskStatusTransitionException extends RuntimeException {
    public InvalidTaskStatusTransitionException(String message) {
        super(message);
    }
}
