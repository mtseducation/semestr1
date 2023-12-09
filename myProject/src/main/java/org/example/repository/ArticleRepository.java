package org.example.repository;

import org.example.domain.Article;

import java.util.List;

public interface ArticleRepository {
    long generateId();
    void createArticle(Article article);
    Article findArticleById(Article.ArticleId articleId);
    List<Article> findAllArticles();
    void updateArticle(Article updatedArticle);
    void deleteArticle(Article.ArticleId articleId);
}
