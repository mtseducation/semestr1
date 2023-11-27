package org.example.repository;

import org.example.domain.Article;
import org.example.domain.Comment;
import org.example.domain.exception.AddCommentToArticleException;
import org.example.domain.exception.DeleteCommentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.domain.Comment.CommentId;

public class InMemoryCommentRepository implements CommentRepository {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryArticleRepository.class);
    private final Map<CommentId, Comment> commentsMap = new ConcurrentHashMap<>();

    @Override
    public int getCommentCountByArticleId(Article.ArticleId articleId) {
        return (int) commentsMap.values().stream()
                         .filter(comment -> comment.getArticleId().equals(articleId))
                         .count();
    }

    @Override
    public List<Comment> getCommentsByArticleId(Article.ArticleId articleId) {
        return commentsMap.values().stream()
                   .filter(comment -> comment.getArticleId().equals(articleId))
                   .toList();
    }

    @Override
    public void deleteCommentByArticleId(Article.ArticleId articleId, CommentId commentId) {
        final var commentsByArticleId = getCommentsByArticleId(articleId);
        if (commentsByArticleId != null) {
            final var commentList = commentsByArticleId.stream()
                                        .map(Comment::getCommentId)
                                        .filter(id -> id.equals(commentId))
                                        .toList();
            commentList.forEach(this::deleteComment);
            LOG.warn("Comment deleted: {}", commentId);
        } else {
            throw new DeleteCommentException("Cannot find comment by articleId");
        }
    }

    @Override
    public void deleteComment(CommentId commentId) {
        if (commentsMap.get(commentId) == null) {
            throw new DeleteCommentException("Cannot find comment by id=" + commentId);
        }
        commentsMap.remove(commentId);
        LOG.debug("Comment deleted: {}", commentId);
    }

    @Override
    public synchronized void addComment(Comment comment) {
        if (commentsMap.get(comment.getCommentId()) != null) {
            throw new AddCommentToArticleException("Comment with the given id already exists: " + comment.getCommentId());
        }
        commentsMap.put(comment.getCommentId(), comment);
        LOG.debug("Comment added: {}", comment.getCommentId().toString());
    }
}
