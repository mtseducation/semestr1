package org.example.service;

import com.fasterxml.jackson.annotation.JacksonInject;
import org.example.domain.Article;
import org.example.domain.Article.ArticleId;
import org.example.domain.ArticleWithCommentCount;
import org.example.domain.Comment;
import org.example.domain.Comment.CommentId;
import org.example.domain.exception.AddCommentToArticleException;
import org.example.domain.exception.AllArticlesWithCommentException;
import org.example.domain.exception.ArticleCreateException;
import org.example.domain.exception.ArticleWithCommentsException;
import org.example.domain.exception.DeleteArticleException;
import org.example.domain.exception.UpdateArticleException;
import org.example.repository.ArticleRepository;
import org.example.repository.CommentRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class ArticleService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public ArticleService(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    public List<Article> getAllArticlesWithComments() {
        List<Article> articles = articleRepository.getAllArticles();
        if (articles != null) {
            for (Article article : articles) {
                List<Comment> comments = commentRepository.getCommentsByArticleId(article.getArticleId());
                article.setCommentList(comments);
            }
            return articles;
        } else {
            throw new AllArticlesWithCommentException("Cannot find any articles");
        }
    }

    public List<ArticleWithCommentCount> getAllArticlesWithCommentCount() {
        List<Article> articles = articleRepository.getAllArticles();
        if (articles != null) {
            return articles.stream()
                       .map(article -> new ArticleWithCommentCount(
                           article,
                           commentRepository.getCommentCountByArticleId(article.getArticleId())))
                       .collect(Collectors.toList());
        } else {
            throw new AllArticlesWithCommentException("Cannot find all article");
        }
    }

    public Article getArticleById(ArticleId articleId) {
        return articleRepository.getArticleById(articleId);
    }

    public List<Comment> getArticleWithComments(ArticleId articleId) {
        final var article = articleRepository.getArticleById(articleId);
        if (article != null) {
            return commentRepository.getCommentsByArticleId(article.getArticleId());
        } else {
            throw new ArticleWithCommentsException("Cannot find article by id");
        }
    }

    public Article updateArticle(ArticleId articleId, String title, Set<String> tags) {
        final var existingArticle = articleRepository.getArticleById(articleId);
        try {
            existingArticle.setTitle(title);
            existingArticle.setTags(tags);
            articleRepository.updateArticle(existingArticle);
            return existingArticle;
        } catch (UpdateArticleException e) {
            throw new UpdateArticleException("Cannot find article by id", e.getCause());
        }
    }

    public void deleteArticle(ArticleId articleId) {
        try {
            articleRepository.deleteArticle(articleId);
        } catch (DeleteArticleException e) {
            throw new DeleteArticleException("Cannot find article by id", e.getCause());
        }
    }

    public ArticleId createArticle(String title, Set<String> tags) {
        try {
            final var articleId = new ArticleId(System.currentTimeMillis());
            final var article = new Article(articleId, title, tags, emptyList());
            articleRepository.createArticle(article);
            return articleId;
        } catch (ArticleCreateException e) {
            throw new ArticleCreateException("Cannon create article", e.getCause());
        }
    }

    public CommentId addCommentToArticle(ArticleId articleId, String text) {
        try {
            final var commentId = new CommentId(System.currentTimeMillis());
            final var comment = new Comment(commentId, articleId, text);
            commentRepository.addComment(comment);
            final var article = articleRepository.getArticleById(articleId);
            article.setCommentList(List.of(comment));
            return commentId;
        } catch (ArticleCreateException exception) {
            throw new AddCommentToArticleException("Cannot find article by id", exception.getCause());
        }
    }

    public void deleteCommentFromArticle(ArticleId articleId, CommentId commentId) {
        commentRepository.deleteCommentByArticleId(articleId, commentId);
    }
}
