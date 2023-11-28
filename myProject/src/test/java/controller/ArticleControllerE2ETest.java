package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Application;
import org.example.controller.ArticleController;
import org.example.controller.response.AllArticlesWithCommentResponse;
import org.example.controller.response.ArticleCreateResponse;
import org.example.domain.Article;
import org.example.domain.Comment;
import org.example.repository.ArticleRepository;
import org.example.repository.InMemoryArticleRepository;
import org.example.repository.InMemoryCommentRepository;
import org.example.service.ArticleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

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
    void shouldCreateArticle() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        final var articleService = Mockito.mock(ArticleService.class);
        Application application = new Application(
            List.of(
                new ArticleController(
                    service,
                    articleService,
                    objectMapper)
            )
        );
        final var articleId = new Article.ArticleId(12L);
        Mockito.when(articleService.createArticle("My Title", Set.of("my_first_tag", "my_first_test_tsg")))
            .thenReturn(articleId);
        application.start();
        service.awaitInitialization();

        // Создаем статью.
        HttpResponse<String> responseCreateArticle = HttpClient.newHttpClient()
                                                         .send(
                                                             HttpRequest.newBuilder()
                                                                 .POST(HttpRequest.BodyPublishers.ofString(
                                                                     """
                                                                         {
                                                                           "title": "My Title",
                                                                           "tags": ["my_first_tag", "my_first_test_tsg"]
                                                                         }
                                                                         """
                                                                 ))
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
        assertEquals(articleId.value(), articleCreateResponse.id().value());

//        final var updateBody = String.format("""
//            {
//              "articleId": "%s",
//              "title": "New title",
//              "tags": ["my tag"]
//            }
//            """, articleId);
//        HttpResponse<String> responseUpdateArticle = HttpClient.newHttpClient()
//                                                         .send(
//                                                             HttpRequest.newBuilder()
//                                                                 .PUT(HttpRequest.BodyPublishers.ofString(updateBody))
//                                                                 .uri(URI.create("http://localhost:%d/api/articles/%s".formatted(service.port(), articleId.toString())))
//                                                                 .header("Content-Type", "application/json")
//                                                                 .build(),
//                                                             HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
//                                                         );
//
//        assertEquals(201, responseUpdateArticle.statusCode());

        HttpResponse<String> responseAllArticlesWithComments = HttpClient.newHttpClient()
                                                                   .send(
                                                                       HttpRequest.newBuilder()
                                                                           .GET()
                                                                           .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                                                                           .header("Content-Type", "application/json")
                                                                           .build(),
                                                                       HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                                                                   );
        assertEquals(201, responseAllArticlesWithComments.statusCode());
        AllArticlesWithCommentResponse allArticlesWithCommentsResponse = objectMapper.readValue(
            responseAllArticlesWithComments.body(),
            AllArticlesWithCommentResponse.class
        );
        assertEquals(articleId, allArticlesWithCommentsResponse.articleList().get(0).getArticleId());

    }

    @Test
    void shouldUpdateArticle() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        final var articleService = Mockito.mock(ArticleService.class);
        Application application = new Application(
            List.of(
                new ArticleController(
                    service,
                    articleService,
                    objectMapper)
            )
        );
        final var articleId = new Article.ArticleId(12L);
        Mockito.when(articleService.updateArticle(articleId, "New title", Set.of("new_tag")));
        final var updateBody = String.format("""
            {
              "articleId": "%s",
              "title": "New title",
              "tags": ["my_first_tag", "my_first_test_tsg"]
            }
            """, articleId);
        HttpResponse<String> responseUpdateArticle = HttpClient.newHttpClient()
                                                         .send(
                                                             HttpRequest.newBuilder()
                                                                 .PUT(HttpRequest.BodyPublishers.ofString(updateBody))
                                                                 .uri(URI.create("http://localhost:%d/api/articles/%s".formatted(service.port(), articleId.toString())))
                                                                 .header("Content-Type", "application/json")
                                                                 .build(),
                                                             HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                                                         );
        assertEquals(201, responseUpdateArticle.statusCode());
    }

    @Test
    void shouldAddComment() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        final var articleRep = Mockito.mock(InMemoryArticleRepository.class);
        final var commentRep = Mockito.mock(InMemoryCommentRepository.class);
//        final var articleService = Mockito.mock(ArticleService.class);
        final var articleService = new ArticleService(articleRep, commentRep);
        Application application = new Application(
            List.of(
                new ArticleController(
                    service,
                    articleService,
                    objectMapper)
            )
        );
        final var articleId = new Article.ArticleId(12L);
        articleService.createArticle("New Title", Set.of("my_first_tag", "my_first_test_tsg"));
        final var commentId = new Comment.CommentId(13L);
        Mockito.when(articleService.addCommentToArticle(articleId, "my first comment")).thenReturn(commentId);
//        Mockito.doNothing().when(articleService).addCommentToArticle(articleId, "my first comment");
        application.start();
        service.awaitInitialization();


        // Добавляем в нее комментарий.
        final var requestBodyForAddComment = String.format(
            """
                {
                  "articleId": "%d",
                  "text": "my first comment"
                }
                """, articleId.value()
        );
        HttpResponse<String> responseAddCommentToArticle =
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(requestBodyForAddComment))
                        .uri(URI.create("http://localhost:%d/api/articles/%d/comments".formatted(service.port(), articleId.value())))
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        assertEquals(201, responseAddCommentToArticle.statusCode());
    }
}
