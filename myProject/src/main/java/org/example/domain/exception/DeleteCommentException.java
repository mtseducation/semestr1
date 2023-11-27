package org.example.domain.exception;

public class DeleteCommentException extends RuntimeException {
    public DeleteCommentException(String message) {
        super(message);
    }

    public DeleteCommentException(String message, Throwable cause) {
        super(message, cause);
    }
}