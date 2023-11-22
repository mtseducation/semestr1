package org.example.controller;

import org.example.service.ArticleService;
import spark.ModelAndView;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;


public class ArticleFreemarkerController implements Controller {
    private final Service service;
    private final ArticleService articleService;
    private final FreeMarkerEngine freeMarkerEngine;

    public ArticleFreemarkerController(Service service, ArticleService articleService, FreeMarkerEngine freeMarkerEngine) {
        this.service = service;
        this.articleService = articleService;
        this.freeMarkerEngine = freeMarkerEngine;
    }

    @Override
    public void initializeEndpoints() {
        getAllArticlesAndComments();
    }

    private void getAllArticlesAndComments() {
        service.get("/api/articles-html", (req, res) -> {
            final var articles = articleService.getAllArticlesWithCommentCount();
            Map<String, Object> model = new HashMap<>();
            model.put("articles", articles);
            return new ModelAndView(model, "templates/Article.ftl");
        }, freeMarkerEngine);
    }
}
