package repository;

import com.typesafe.config.ConfigFactory;
import org.example.domain.Article;
import org.example.domain.Comment;
import org.example.repository.postgres.PostgresArticleRepository;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleRepositoryTest {
    private static Jdbi jdbi;

    @BeforeAll
    static void beforeAll() {
        final var config = ConfigFactory.load();
        final var flyway =
            Flyway.configure()
                .outOfOrder(true)
                .locations("classpath:db/migrations")
                .dataSource(config.getString("app.database.url"), config.getString("app.database.user"),
                    config.getString("app.database.password"))
                .load();
        flyway.migrate();
        jdbi = Jdbi.create(config.getString("app.database.url"), config.getString("app.database.user"),
            config.getString("app.database.password"));
    }

    @BeforeEach
    void beforeEach() {
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article").execute());
    }

    @Test
    void shouldCreateNewArticle() {
        PostgresArticleRepository repository = new PostgresArticleRepository(jdbi);

        final var articleId = repository.generateId();
        final var id = new Article.ArticleId(articleId);
        repository.createArticle(new Article(
            id,
            "My Title",
            Set.of("my_first_tag", "my_first_test_tsg"),
            Collections.emptyList()
        ));
        final var article = repository.findArticleById(id);
        assertEquals(articleId, article.getArticleId().value());
    }
}
