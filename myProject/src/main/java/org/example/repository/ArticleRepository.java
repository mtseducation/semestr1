package org.example.repository;

import org.example.domain.Article;

import java.util.List;

public interface ArticleRepository {
    void createArticle(Article article);
    Article getArticleById(Article.ArticleId articleId);
    List<Article> getAllArticles();
    void updateArticle(Article updatedArticle);
    void deleteArticle(Article.ArticleId articleId);
}
