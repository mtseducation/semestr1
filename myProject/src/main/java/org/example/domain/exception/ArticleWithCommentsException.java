package org.example.domain.exception;

public class ArticleWithCommentsException extends RuntimeException {
    public ArticleWithCommentsException(String message) {
        super(message);
    }

    public ArticleWithCommentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
