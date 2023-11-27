package org.example.domain.exception;

public class ArticleCreateException extends RuntimeException {

    public ArticleCreateException(String message) {
        super(message);
    }

    public ArticleCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
