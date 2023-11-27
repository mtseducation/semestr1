package org.example.domain.exception;

public class UpdateArticleException extends RuntimeException {
    public UpdateArticleException(String message) {
        super(message);
    }

    public UpdateArticleException(String message, Throwable cause) {
        super(message, cause);
    }
}
