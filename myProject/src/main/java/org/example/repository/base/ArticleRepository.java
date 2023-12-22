package org.example.repository.base;

import org.example.domain.Article;

import java.sql.SQLException;
import java.util.List;

public interface ArticleRepository {
    Article.ArticleId generateId();
    void createArticle(Article article);
    Article findArticleById(Article.ArticleId articleId) throws SQLException;
    List<Article> findAllArticles();
    void updateArticle(Article updatedArticle);
    void deleteArticle(Article.ArticleId articleId);
    boolean isTrending(Article.ArticleId articleId);
}
