package org.example.domain.exception;

public class DeleteArticleException extends RuntimeException {
    public DeleteArticleException(String message) {
        super(message);
    }

    public DeleteArticleException(String message, Throwable cause) {
        super(message, cause);
    }
}