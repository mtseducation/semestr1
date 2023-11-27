package org.example.controller.request;

import org.example.domain.Article;

public record AddCommentToArticleRequest(Article.ArticleId articleId, String text) {
}
