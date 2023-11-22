package org.example.controller;

import org.example.domain.Article;
import org.example.domain.Comment;
import org.example.service.ArticleService;
import org.example.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

public class ArticleController implements Controller {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);
    private final Service service;
    private final ArticleService articleService;

    public ArticleController(Service service, ArticleService articleService) {
        this.service = service;
        this.articleService = articleService;
        setupEndpoints();
    }

    @Override
    public void initializeEndpoints() {
        setupEndpoints();
    }

    private void setupEndpoints() {
        service.get("/api/articles", (req, res) -> {
            try {
                LOG.debug("GET request for /api/articles - OK");
                res.status(201);
                return articleService.getAllArticlesWithComments();
            } catch (Exception e) {
                LOG.warn("Warning cannot GET request for /api/articles", e);
                res.status(400);
                throw e;
            }
        }, JsonUtil.json());

        service.get("/api/articles/:id", (req, res) -> {
            try {
                long articleId = Long.parseLong(req.params("id"));
                LOG.debug("GET request for /api/articles/:{} - OK", articleId);
                return articleService.getArticleWithComments(articleId);
            } catch (Exception e) {
                LOG.error("Error GET request for /api/articles/", e);
                throw e;
            }
        }, JsonUtil.json());

        service.put("/api/articles/:id", (req, res) -> {
            long articleId = Long.parseLong(req.params("id"));
            final var updatedArticle = JsonUtil.fromJson(req.body(), Article.class);
            return articleService.updateArticle(articleId, updatedArticle);
        }, JsonUtil.json());

        service.delete("/api/articles/:id", (req, res) -> {
            long articleId = Long.parseLong(req.params("id"));
            articleService.deleteArticle(articleId);
            return "Article deleted";
        });

        service.post("/api/articles", (req, res) -> {
            Article newArticle = JsonUtil.fromJson(req.body(), Article.class);
            return articleService.addArticle(newArticle);
        }, JsonUtil.json());

        service.post("/api/articles/:id/comments", (req, res) -> {
            long articleId = Long.parseLong(req.params("id"));
            final var newComment = JsonUtil.fromJson(req.body(), Comment.class);
            return articleService.addCommentToArticle(articleId, newComment);
        }, JsonUtil.json());

        service.delete("/api/articles/:articleId/comments/:commentId", (req, res) -> {
            long articleId = Long.parseLong(req.params("articleId"));
            long commentId = Long.parseLong(req.params("commentId"));
            articleService.deleteCommentFromArticle(articleId, commentId);
            return "Comment deleted";
        });
    }
}
