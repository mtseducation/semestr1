package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.request.AddCommentToArticleRequest;
import org.example.controller.request.ArticleCreateRequest;
import org.example.controller.request.ArticleRequest;
import org.example.controller.request.DeleteArticleRequest;
import org.example.controller.request.DeleteCommentRequest;
import org.example.controller.request.UpdateArticleRequest;
import org.example.controller.response.AllArticlesWithCommentResponse;
import org.example.controller.response.ArticleCreateResponse;
import org.example.controller.response.ArticleResponse;
import org.example.controller.response.ArticleWithCommentsResponse;
import org.example.controller.response.ErrorResponse;
import org.example.domain.exception.AddCommentToArticleException;
import org.example.domain.exception.AllArticlesWithCommentException;
import org.example.domain.exception.ArticleCreateException;
import org.example.domain.exception.ArticleWithCommentsException;
import org.example.domain.exception.DeleteArticleException;
import org.example.domain.exception.DeleteCommentException;
import org.example.domain.exception.UpdateArticleException;
import org.example.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

public class ArticleController implements Controller {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);
    private final Service service;
    private final ArticleService articleService;
    private final ObjectMapper objectMapper;

    public ArticleController(Service service, ArticleService articleService, ObjectMapper objectMapper) {
        this.service = service;
        this.articleService = articleService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void initializeEndpoints() {
        allArticlesWithComments();
        articleWithComments();
        updateArticle();
        deleteArticle();
        createArticle();
        addCommentToArticle();
        deleteComment();
    }

    private void allArticlesWithComments() {
        service.get("/api/articles", (req, res) -> {
            res.type("application/json");
            try {
                final var articlesWithComments = articleService.getAllArticlesWithComments();
                res.status(201);
                return objectMapper.writeValueAsString(new AllArticlesWithCommentResponse(articlesWithComments));
            } catch (AllArticlesWithCommentException e) {
                LOG.warn("Cannot GET request for /api/articles");
                res.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private void articleWithComments() {
        service.get("/api/articles/:id", (req, res) -> {
            res.type("application/json");
            ArticleRequest articleRequest = objectMapper.readValue(req.body(), ArticleRequest.class);
            try {
                final var article = articleService.getArticleById(articleRequest.articleId());
                final var articleWithComments = articleService.getArticleWithComments(articleRequest.articleId());
                res.status(201);
                return objectMapper.writeValueAsString(
                    new ArticleWithCommentsResponse(
                        article.getTitle(),
                        articleWithComments)
                );
            } catch (ArticleWithCommentsException e) {
                LOG.warn("Exception GET request for /api/articles/", e);
                res.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private void updateArticle() {
        service.put("/api/articles/:id", (req, res) -> {
            res.type("application/json");
            UpdateArticleRequest updateArticleRequest = objectMapper.readValue(req.body(), UpdateArticleRequest.class);
            try {
                final var updatedArticle = articleService.updateArticle(
                    updateArticleRequest.articleId(),
                    updateArticleRequest.title(),
                    updateArticleRequest.tags()
                );
                res.status(201);
                return objectMapper.writeValueAsString(new ArticleResponse(
                    updatedArticle.getTitle(),
                    updatedArticle.getTags(),
                    updatedArticle.getCommentList()
                ));
            } catch (UpdateArticleException exception) {
                LOG.warn("Cannot update article");
                res.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(exception.getMessage()));
            }
        });
    }

    private void deleteArticle() {
        service.delete("/api/articles/:id", (req, res) -> {
            res.type("application/json");
            DeleteArticleRequest deleteArticleRequest = objectMapper.readValue(req.body(), DeleteArticleRequest.class);
            try {
                articleService.deleteArticle(deleteArticleRequest.articleId());
                res.status(201);
                LOG.info("Article deleted");
                return objectMapper.writeValueAsString(new ArticleCreateResponse(deleteArticleRequest.articleId()));
            } catch (DeleteArticleException exception) {
                LOG.warn("Cannon delete article");
                res.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(exception.getMessage()));
            }
        });
    }

    private void createArticle() {
        service.post("/api/articles", (req, res) -> {
            res.type("application/json");
            ArticleCreateRequest article = objectMapper.readValue(req.body(), ArticleCreateRequest.class);
            try {
                final var articleId = articleService.createArticle(
                    article.title(),
                    article.tags()
                );
                res.status(201);
                return objectMapper.writeValueAsString(new ArticleCreateResponse(articleId));
            } catch (ArticleCreateException exception) {
                LOG.warn("Cannot create articles");
                res.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(exception.getMessage()));
            }
        });
    }

    private void addCommentToArticle() {
        service.post("/api/articles/:id/comments", (req, res) -> {
            res.type("application/json");
            AddCommentToArticleRequest articleRequest = objectMapper.readValue(req.body(), AddCommentToArticleRequest.class);
            try {
                articleService.addCommentToArticle(articleRequest.articleId(), articleRequest.text());
                res.status(201);
                final var article = articleService.getArticleById(articleRequest.articleId());
                final var articleWithComments = articleService.getArticleWithComments(articleRequest.articleId());
                return objectMapper.writeValueAsString(
                    new ArticleWithCommentsResponse(
                        article.getTitle(),
                        articleWithComments)
                );
            } catch (AddCommentToArticleException exception) {
                LOG.warn("Cannot add comment to article");
                res.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(exception.getMessage()));
            }
        });
    }


    private void deleteComment() {
        service.delete("/api/articles/:articleId/comments/:commentId", (req, res) -> {
            res.type("application/json");
            DeleteCommentRequest deleteCommentRequest = objectMapper.readValue(req.body(), DeleteCommentRequest.class);
            try {
                articleService.deleteCommentFromArticle(
                    deleteCommentRequest.articleId(),
                    deleteCommentRequest.commentId()
                );
                res.status(201);
                LOG.warn("Comment deleted");
                return objectMapper.writeValueAsString(new ArticleCreateResponse(deleteCommentRequest.articleId()));
            } catch (DeleteCommentException exception) {
                LOG.warn("Cannot deleted comment");
                res.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(exception.getMessage()));
            }
        });
    }
}
