package org.example.controller.response;

import org.example.domain.Comment;

public record CommentCreateResponse(Comment.CommentId commentId) {
}
