package org.example.controller.request;

import org.example.domain.Article;

public record DeleteArticleRequest(Article.ArticleId articleId) {
}
