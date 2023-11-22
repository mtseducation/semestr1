package org.example.repository;

import org.example.domain.Comment;
import org.example.domain.Comment.CommentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.domain.Article.ArticleId;


public class CommentRepository {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleRepository.class);

    private final Map<CommentId, Comment> comments = new ConcurrentHashMap<>();

    public void addComment(Comment comment) {
        comments.put(comment.getCommentId(), comment);
        LOG.debug("Comment added: {}", comment.getCommentId());
    }

    public Comment getCommentById(CommentId commentId) {
        return comments.get(commentId);
    }

    public List<Comment> getAllComments() {
        return new ArrayList<>(comments.values());
    }

    public void updateComment(CommentId commentId, Comment updatedComment) {
        comments.put(commentId, updatedComment);
        LOG.debug("Comment updated: {}", commentId);
    }

    public void deleteComment(CommentId commentId) {
        comments.remove(commentId);
        LOG.warn("Comment deleted: {}", commentId);
    }

    public List<Comment> getCommentsByArticleId(ArticleId articleId) {
        return comments.values().stream()
                   .filter(comment -> comment.getArticleId().equals(articleId))
                   .toList();
    }

    public int getCommentCountByArticleId(ArticleId articleId) {
        return (int) comments.values().stream()
                         .filter(comment -> comment.getArticleId().equals(articleId))
                         .count();
    }

}
