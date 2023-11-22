package org.example;

import org.example.controller.ArticleController;
import org.example.controller.ArticleFreemarkerController;
import org.example.repository.ArticleRepository;
import org.example.repository.CommentRepository;
import org.example.service.ArticleService;
import org.example.template.TemplateFactory;
import spark.Service;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Service service = Service.ignite();
        final var articleService = new ArticleService(new ArticleRepository(), new CommentRepository());
        Application application = new Application(
            List.of(
                new ArticleController(
                    service,
                    articleService
                ),
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