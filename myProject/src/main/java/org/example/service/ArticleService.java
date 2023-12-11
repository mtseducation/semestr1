package org.example.service;

import org.example.controller.response.ArticleWithCommentCount;
import org.example.domain.Article;
import org.example.domain.Article.ArticleId;
import org.example.domain.Comment;
import org.example.domain.Comment.CommentId;
import org.example.domain.exception.AddCommentToArticleException;
import org.example.domain.exception.AllArticlesWithCommentException;
import org.example.domain.exception.ArticleCreateException;
import org.example.domain.exception.ArticleWithCommentsException;
import org.example.domain.exception.DeleteArticleException;
import org.example.domain.exception.UpdateArticleException;
import org.example.repository.base.ArticleRepository;
import org.example.repository.base.CommentRepository;

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

    public List<Article> findAllArticlesWithComments() {
        List<Article> articles = articleRepository.findAllArticles();
        if (articles != null) {
            for (Article article : articles) {
                List<Comment> comments = commentRepository.findCommentsByArticleId(article.getArticleId());
                article.setCommentList(comments);
            }
            return articles;
        } else {
            throw new AllArticlesWithCommentException("Cannot find any articles");
        }
    }

    public List<ArticleWithCommentCount> findAllArticlesWithCommentCount() {
        List<Article> articles = articleRepository.findAllArticles();
        if (articles != null) {
            return articles.stream()
                       .map(article -> new ArticleWithCommentCount(
                           article,
                           commentRepository.findCommentCountByArticleId(article.getArticleId())))
                       .collect(Collectors.toList());
        } else {
            throw new AllArticlesWithCommentException("Cannot find all article");
        }
    }

    public Article findArticleById(ArticleId articleId) {
        return articleRepository.findArticleById(articleId);
    }

    public List<Comment> findArticleWithComments(ArticleId articleId) {
        final var article = articleRepository.findArticleById(articleId);
        if (article != null) {
            return commentRepository.findCommentsByArticleId(article.getArticleId());
        } else {
            throw new ArticleWithCommentsException("Cannot find article by id");
        }
    }

    public Article updateArticle(ArticleId articleId, String title, Set<String> tags) {
        final var existingArticle = articleRepository.findArticleById(articleId);
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
            final var articleId = new ArticleId(articleRepository.generateId());
            final var article = new Article(articleId, title, tags, emptyList());
            articleRepository.createArticle(article);
            return articleId;
        } catch (ArticleCreateException e) {
            throw new ArticleCreateException("Cannon create article", e.getCause());
        }
    }

    public void createCommentToArticle(ArticleId articleId, String text) {
        try {
            final var commentId = new CommentId(commentRepository.generateId());
            final var comment = new Comment(commentId, articleId, text);
            commentRepository.createComment(comment);
            final var article = articleRepository.findArticleById(articleId);
            article.setCommentList(List.of(comment));
        } catch (ArticleCreateException exception) {
            throw new AddCommentToArticleException("Cannot find article by id", exception.getCause());
        }
    }

    public void deleteCommentFromArticle(ArticleId articleId, CommentId commentId) {
        commentRepository.deleteCommentByArticleId(articleId, commentId);
    }
}
