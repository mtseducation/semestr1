package org.example.controller.response;

import org.example.domain.Article;

public record ArticleWithCommentCount(Article article, int commentCount) {
}
