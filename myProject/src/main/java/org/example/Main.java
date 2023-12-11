package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import org.example.controller.ArticleController;
import org.example.controller.ArticleFreemarkerController;
import org.example.repository.inMemory.InMemoryArticleRepository;
import org.example.repository.inMemory.InMemoryCommentRepository;
import org.example.service.ArticleService;
import org.example.template.TemplateFactory;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.util.List;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        final var config = ConfigFactory.load();
        final var flyway = Flyway.configure()
                               .outOfOrder(true)
                               .locations("classpath:db/migrations")
                               .dataSource(config.getString("app.database.url"), config.getString("app.database.user"),
                                   config.getString("app.database.password"))
                               .load();
        flyway.migrate();
        Service service = Service.ignite();
        ObjectMapper objectMapper = new ObjectMapper();
        final var articleService = new ArticleService(new InMemoryArticleRepository(), new InMemoryCommentRepository());
        Application application = new Application(
            List.of(
                new ArticleController(
                    service,
                    articleService,
                    objectMapper),
                new ArticleFreemarkerController(
                    service,
                    articleService,
                    TemplateFactory.freeMarkerEngine()
                )
            )
        );
        application.start();
    }
}