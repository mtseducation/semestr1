package org.example.repository.postgres;

import org.example.domain.Article;
import org.example.domain.Comment;
import org.example.repository.base.CommentRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class PostgresCommentRepository implements CommentRepository {
    private final Jdbi jdbi;

    public PostgresCommentRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public long generateId() {
        return (long) jdbi.inTransaction(
            (Handle handle) -> handle.createQuery("SELECT nextval('comment_id_seq') AS value")
                                   .mapToMap()
                                   .first()
                                   .get("value")
        );
    }

    @Override
    public int findCommentCountByArticleId(Article.ArticleId articleId) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT COUNT(*) FROM comment WHERE article_id = ?")
                                             .bind(0, articleId.value())
                                             .mapTo(Integer.class)
                                             .one()
        );
    }

    @Override
    public List<Comment> findCommentsByArticleId(Article.ArticleId articleId) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM comment WHERE article_id = ?")
                                             .bind(0, articleId.value())
                                             .mapTo(Comment.class)
                                             .list()
        );
    }

    @Override
    public void deleteCommentByArticleId(Article.ArticleId articleId, Comment.CommentId commentId) {
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM comment WHERE article_id = ? AND id = ?")
                                     .bind(0, articleId.value())
                                     .bind(1, commentId.value())
                                     .execute()
        );
    }

    @Override
    public void deleteComment(Comment.CommentId commentId) {
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM comment WHERE id = ?")
                                     .bind(0, commentId.value())
                                     .execute()
        );
    }

    @Override
    public void createComment(Comment comment) {
        jdbi.useTransaction(handle -> handle.createUpdate("INSERT INTO comment (id, article_id, text) VALUES (?, ?, ?)")
                                     .bind(0, comment.getCommentId().value())
                                     .bind(1, comment.getArticleId().value())
                                     .bind(2, comment.getText())
                                     .execute()
        );
    }
}
