package org.example.controller.request;

import org.example.domain.Article;

public record ArticleRequest(Article.ArticleId articleId) {
}
