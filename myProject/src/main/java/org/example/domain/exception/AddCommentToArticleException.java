package org.example.domain.exception;

public class AddCommentToArticleException extends RuntimeException {
    public AddCommentToArticleException(String message) {
        super(message);
    }

    public AddCommentToArticleException(String message, Throwable cause) {
        super(message, cause);
    }
}
