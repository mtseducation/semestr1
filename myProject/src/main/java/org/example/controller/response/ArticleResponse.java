package org.example.controller.response;

import org.example.domain.Comment;

import java.util.List;
import java.util.Set;

public record ArticleResponse(String title, Set<String> tags, List<Comment> commentList) {
}
