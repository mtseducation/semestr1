package org.example.controller.response;

import org.example.domain.Comment;

import java.util.List;

public record ArticleWithCommentsResponse(String title, List<Comment> commentList) {
}
