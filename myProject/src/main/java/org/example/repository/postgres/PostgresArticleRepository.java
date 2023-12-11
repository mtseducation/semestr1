package org.example.repository.postgres;

import org.example.domain.Article;
import org.example.repository.base.ArticleRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class PostgresArticleRepository implements ArticleRepository {
    private final Jdbi jdbi;

    public PostgresArticleRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public long generateId() {
        return (long) jdbi.inTransaction(
            (Handle handle) -> handle.createQuery("SELECT nextval('article_id_seq') AS value")
                                   .mapToMap()
                                   .first()
                                   .get("value")
        );
    }

    @Override
    public void createArticle(Article article) {
        jdbi.useTransaction(handle -> handle.createUpdate("INSERT INTO article (id, title, tags, trending) VALUES (?, ?, ?, ?)")
                                          .bind(0, article.getArticleId().value())
                                          .bind(1, article.getTitle())
                                          .bind(2, article.getTags().toArray(new String[0]))
                                          .bind(3, false)
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
        jdbi.useTransaction(handle -> handle.createUpdate("UPDATE article SET title = ?, tags = ?, trending = ? WHERE id = ?")
                                          .bind(0, updatedArticle.getTitle())
                                          .bind(1, updatedArticle.getTags().toArray(new String[0]))
                                          .bind(2, trending)
                                          .bind(3, updatedArticle.getArticleId().value())
                                          .execute());
    }

    @Override
    public void deleteArticle(Article.ArticleId articleId) {
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article where id = ?")
                                          .bind(0, articleId.value())
                                          .execute()
        );
    }

    @Override
    public boolean isTrending(Article.ArticleId articleId) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT COUNT(*) FROM comment WHERE article_id = ?")
                                             .bind(0, articleId.value())
                                             .mapTo(Integer.class)
                                             .findOne()
                                             .orElse(0) >= 3);
    }
}
