package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Application;
import org.example.controller.ArticleController;
import org.example.controller.response.ArticleCreateResponse;
import org.example.controller.response.ArticleResponse;
import org.example.controller.response.ArticleWithCommentsResponse;
import org.example.repository.inMemory.InMemoryArticleRepository;
import org.example.repository.inMemory.InMemoryCommentRepository;
import org.example.service.ArticleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ArticleControllerE2ETest {

    private Service service;

    @BeforeEach
    void beforeEach() {
        service = Service.ignite();
    }

    @AfterEach
    void afterEach() {
        service.stop();
        service.awaitStop();
    }

    @Test
    void shouldCreateArticle() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        final var articleRepository = new InMemoryArticleRepository();
        final var commentRepository = new InMemoryCommentRepository();
        final var articleService = new ArticleService(articleRepository, commentRepository);
        Application application = new Application(
            List.of(
                new ArticleController(
                    service,
                    articleService,
                    objectMapper)
            )
        );
        application.start();
        service.awaitInitialization();

        // Создаем статью.
        final var body = """
            {
              "title": "My Title",
              "tags": ["my_first_tag", "my_first_test_tsg"]
            }
            """;
        HttpResponse<String> responseCreateArticle =
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        // проверка что статья создалась
        assertEquals(201, responseCreateArticle.statusCode());
        ArticleCreateResponse articleCreateResponse = objectMapper.readValue(
            responseCreateArticle.body(),
            ArticleCreateResponse.class
        );
        final var articleId = articleCreateResponse.id().value();

        // Добавляем в нее комментарий.
        final var requestBodyForAddComment = String.format(
            """
                {
                  "articleId": "%s",
                  "text": "my first comment"
                }
                """, articleId
        );
        HttpResponse<String> responseAddCommentToArticle =
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(requestBodyForAddComment))
                        .uri(URI.create("http://localhost:%d/api/articles/%s/comments".formatted(service.port(), articleId)))
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        assertEquals(201, responseAddCommentToArticle.statusCode());

        ArticleWithCommentsResponse articleWithCommentsResponse = objectMapper.readValue(
            responseAddCommentToArticle.body(),
            ArticleWithCommentsResponse.class
        );
        assertEquals(1, articleWithCommentsResponse.commentList().size());
        assertEquals("my first comment", articleWithCommentsResponse.commentList().get(0).getText());
        final var commentId = articleWithCommentsResponse.commentList().get(0).getCommentId();

        final var updateBody = String.format("""
            {
              "articleId": "%s",
              "title": "New title",
              "tags": ["my tag"]
            }
            """, articleId);
        HttpResponse<String> responseUpdateArticle =
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .PUT(HttpRequest.BodyPublishers.ofString(updateBody))
                        .uri(URI.create("http://localhost:%d/api/articles/%s".formatted(service.port(), articleId)))
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(201, responseUpdateArticle.statusCode());
        ArticleResponse articleResponse = objectMapper.readValue(
            responseUpdateArticle.body(),
            ArticleResponse.class
        );
        assertEquals("New title", articleResponse.title());

        HttpResponse<String> responseDeleteComment =
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .DELETE()
                        .uri(URI.create("http://localhost:%d/api/articles/%s/comments/%s".formatted(service.port(), articleId, commentId.value())))
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        assertEquals(201, responseDeleteComment.statusCode());


        HttpResponse<String> responseArticleWithComments =
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create("http://localhost:%d/api/articles/%s".formatted(service.port(), articleId)))
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        assertEquals(201, responseArticleWithComments.statusCode());

        ArticleWithCommentsResponse articleWithCommentsResponse1 = objectMapper.readValue(
            responseArticleWithComments.body(),
            ArticleWithCommentsResponse.class
        );
        assertEquals(0, articleWithCommentsResponse1.commentList().size());
        assertEquals(articleResponse.title(), articleWithCommentsResponse1.title());
    }
}
