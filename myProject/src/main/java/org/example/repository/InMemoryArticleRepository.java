package org.example.repository;

import org.example.domain.Article;
import org.example.domain.exception.ArticleCreateException;
import org.example.domain.exception.DeleteArticleException;
import org.example.domain.exception.UpdateArticleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryArticleRepository implements ArticleRepository {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryArticleRepository.class);
    private final Map<Article.ArticleId, Article> articlesMap = new ConcurrentHashMap<>();

    @Override
    public synchronized void createArticle(Article article) {
        if (articlesMap.get(article.getArticleId()) != null) {
            throw new ArticleCreateException("Article with the given id already exists: " + article.getArticleId());
        }
        articlesMap.put(article.getArticleId(), article);
        LOG.debug("Article added: {}", article.getArticleId().toString());
    }

    @Override
    public Article getArticleById(Article.ArticleId articleId) {
        final var article = articlesMap.get(articleId);
        if (article == null) {
            throw new ArticleCreateException("Cannot find article by id=" + articleId);
        }
        LOG.debug("Find Article by id: {}", articleId);
        return article;
    }

    @Override
    public List<Article> getAllArticles() {
        return new ArrayList<>(articlesMap.values());
    }

    @Override
    public synchronized void updateArticle(Article updatedArticle) {
        if (articlesMap.get(updatedArticle.getArticleId()) == null) {
            throw new UpdateArticleException("Cannot find article by id=" + updatedArticle.getArticleId());
        }
        articlesMap.put(updatedArticle.getArticleId(), updatedArticle);
        LOG.debug("Article updated for id: {}", updatedArticle.getArticleId());
    }

    @Override
    public void deleteArticle(Article.ArticleId articleId) {
        if (articlesMap.get(articleId) == null) {
            throw new DeleteArticleException("Cannot find article by id=" + articleId);
        }
        articlesMap.remove(articleId);
        LOG.debug("Article deleted: {}", articleId);
    }
}
