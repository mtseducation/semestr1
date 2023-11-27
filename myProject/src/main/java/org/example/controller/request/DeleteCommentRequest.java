package org.example.controller.request;

import org.example.domain.Article;
import org.example.domain.Comment;

public record DeleteCommentRequest(Article.ArticleId articleId, Comment.CommentId commentId) {
}
