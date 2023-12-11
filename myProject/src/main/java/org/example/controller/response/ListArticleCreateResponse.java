package org.example.controller.response;

import org.example.domain.Article;

import java.util.List;

public record ListArticleCreateResponse(List<Article.ArticleId> id) {
}
