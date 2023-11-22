package org.example.repository;

import org.example.domain.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.domain.Article.ArticleId;

public class ArticleRepository {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleRepository.class);
    private final Map<ArticleId, Article> articles = new ConcurrentHashMap<>();

    public void addArticle(Article article) {
        articles.put(article.getArticleId(), article);
        LOG.debug("Article added: {}", article.getArticleId().toString());
    }

    public Article getArticleById(ArticleId articleId) {
        return articles.get(articleId);
    }

    public List<Article> getAllArticles() {
        return new ArrayList<>(articles.values());
    }

    public void updateArticle(ArticleId articleId, Article updatedArticle) {
        articles.put(articleId, updatedArticle);
        LOG.debug("Article updated: {}", articleId);
    }

    public void deleteArticle(ArticleId articleId) {
        articles.remove(articleId);
        LOG.warn("Article deleted: {}", articleId);
    }

}
