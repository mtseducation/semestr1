package org.example.repository;

import org.example.domain.Article;
import org.example.domain.Comment;

import java.util.List;

public interface CommentRepository {
    int getCommentCountByArticleId(Article.ArticleId articleId);
    List<Comment> getCommentsByArticleId(Article.ArticleId articleId);
    void deleteCommentByArticleId(Article.ArticleId articleId, Comment.CommentId commentId);
    void deleteComment(Comment.CommentId commentId);
    void addComment(Comment comment);
}
