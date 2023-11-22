package controller;

import org.example.Application;
import org.example.controller.ArticleController;
import org.example.domain.Article;
import org.example.domain.Comment;
import org.example.service.ArticleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import spark.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void shouldItWork() throws Exception {
        final var articleService = Mockito.mock(ArticleService.class);
        Application application = new Application(
            List.of(
                new ArticleController(
                    service,
                    articleService
                )
            )
        );
        final var articleId = new Article.ArticleId(System.currentTimeMillis());
        final var commentId = new Comment.CommentId(System.currentTimeMillis());
        final var articleJson = """
            {
                "articleId": %s,
                "title": "new title",
                "tags": ["#title", "#new"],
                "commentList": [
                    {"commentId": %s, "articleId": %s, "text": "my comment"},
                    {"commentId": %s, "articleId": %s, "text": "one more comment"}
                ]
            }
        """.formatted(articleId, commentId, articleId, commentId, articleId);

        HttpRequest request = HttpRequest.newBuilder()
                                  .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                                  .header("Content-Type", "application/json")
                                  .POST(HttpRequest.BodyPublishers.ofString(articleJson))
                                  .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                                            .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
    }
}
