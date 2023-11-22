package org.example.domain;


public class ArticleWithCommentCount {
    private final Article article;
    private final int commentCount;

    public ArticleWithCommentCount(Article article, int commentCount) {
        this.article = article;
        this.commentCount = commentCount;
    }
}
