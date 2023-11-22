package org.example.domain;

import org.example.domain.Article.ArticleId;

import java.util.Objects;

public class Comment {
    private CommentId commentId;
    private ArticleId articleId;
    private String text;

    public Comment(CommentId commentId, ArticleId articleId, String text) {
        this.commentId = commentId;
        this.articleId = articleId;
        this.text = text;
    }

    public CommentId getCommentId() {
        return commentId;
    }

    public ArticleId getArticleId() {
        return articleId;
    }

    public String getText() {
        return text;
    }

    public void setArticleId(ArticleId articleId) {
        this.articleId = articleId;
    }

    public void setCommentId(CommentId commentId) {
        this.commentId = commentId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static class CommentId {
        private final long id;

        public CommentId(long id) {
            this.id = id;
        }

        public long value() {
            return id;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        return commentId == comment.commentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }
}
