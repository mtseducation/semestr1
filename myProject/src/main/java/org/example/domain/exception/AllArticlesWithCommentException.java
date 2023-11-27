package org.example.domain.exception;

public class AllArticlesWithCommentException extends RuntimeException {
    public AllArticlesWithCommentException(String message) {
        super(message);
    }

    public AllArticlesWithCommentException(String message, Throwable cause) {
        super(message, cause);
    }
}
