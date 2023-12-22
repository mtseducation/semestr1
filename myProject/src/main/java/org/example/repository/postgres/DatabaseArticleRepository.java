package org.example.repository.postgres;

import org.example.domain.Article;
import org.example.domain.Comment;
import org.example.repository.base.ArticleRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.postgresql.jdbc.PgArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseArticleRepository implements ArticleRepository {
    private final Jdbi jdbi;

    public DatabaseArticleRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Article.ArticleId generateId() {
        return new Article.ArticleId((long) jdbi.inTransaction(
            (Handle handle) -> handle.createQuery("SELECT nextval('article_id_seq') AS value")
                                   .mapToMap()
                                   .first()
                                   .get("value")
        ));
    }

    @Override
    public void createArticle(Article article) {
        jdbi.useTransaction(handle -> handle.createUpdate("INSERT INTO article (id, title, tags, trending) " +
                                                              "VALUES (:id, :title, :tags, :trending)")
                                          .bind("id", article.getArticleId().value())
                                          .bind("title", article.getTitle())
                                          .bind("tags", article.getTags().toArray(new String[0]))
                                          .bind("trending", false)
                                          .execute()
        );
    }

    @Override
    public Article findArticleById(Article.ArticleId articleId) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM article WHERE id = :id")
                                             .bind("id", articleId.value())
                                             .mapToBean(Article.class)
                                             .findFirst()
                                             .orElse(null)
        );
    }

    @Override
    public List<Article> findAllArticles() {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM atricle")
                                             .mapToBean(Article.class)
                                             .list()
        );
    }

    @Override
    public void updateArticle(Article updatedArticle) {
        final var trending = isTrending(updatedArticle.getArticleId());
        jdbi.useTransaction(handle -> handle.createUpdate("UPDATE article SET title = :title, " +
                                                              "tags = :tags, trending = :trending WHERE id = :id")
                                          .bind("title", updatedArticle.getTitle())
                                          .bind("tags", updatedArticle.getTags().toArray(new String[0]))
                                          .bind("trending", trending)
                                          .bind("id", updatedArticle.getArticleId().value())
                                          .execute());
    }

    @Override
    public void deleteArticle(Article.ArticleId articleId) {
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article WHERE id = :id")
                                          .bind("id", articleId.value())
                                          .execute()
        );
    }

    @Override
    public boolean isTrending(Article.ArticleId articleId) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT COUNT(*) FROM comment WHERE article_id = :article_id")
                                             .bind("article_id", articleId.value())
                                             .mapTo(Integer.class)
                                             .findOne()
                                             .orElse(0) >= 3);
    }
}
