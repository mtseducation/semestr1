package org.example.controller.request;

import org.example.domain.Article;

import java.util.Set;

public record UpdateArticleRequest(Article.ArticleId articleId, String title, Set<String> tags) {
}
