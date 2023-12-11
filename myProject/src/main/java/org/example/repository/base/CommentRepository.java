package org.example.repository.base;

import org.example.domain.Article;
import org.example.domain.Comment;

import java.util.List;

public interface CommentRepository {
    long generateId();
    int findCommentCountByArticleId(Article.ArticleId articleId);
    List<Comment> findCommentsByArticleId(Article.ArticleId articleId);
    void deleteCommentByArticleId(Article.ArticleId articleId, Comment.CommentId commentId);
    void deleteComment(Comment.CommentId commentId);
    void createComment(Comment comment);
}
