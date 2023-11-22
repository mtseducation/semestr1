package org.example.service;

import org.example.domain.Article;
import org.example.domain.Article.ArticleId;
import org.example.domain.ArticleWithCommentCount;
import org.example.domain.Comment;
import org.example.domain.Comment.CommentId;
import org.example.repository.ArticleRepository;
import org.example.repository.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;


public class ArticleService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public ArticleService(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    public List<Article> getAllArticlesWithComments() {
        List<Article> articles = articleRepository.getAllArticles();
        for (Article article : articles) {
            List<Comment> comments = commentRepository.getCommentsByArticleId(article.getArticleId());
            article.setCommentList(comments);
        }
        return articles;
    }

    public List<ArticleWithCommentCount> getAllArticlesWithCommentCount() {
        List<Article> articles = articleRepository.getAllArticles();
        return articles.stream()
                   .map(article -> new ArticleWithCommentCount(article, commentRepository.getCommentCountByArticleId(article.getArticleId())))
                   .collect(Collectors.toList());
    }

    public Article getArticleWithComments(long articleId) {
        final var article = articleRepository.getArticleById(new ArticleId(articleId));
        if (article != null) {
            List<Comment> comments = commentRepository.getCommentsByArticleId(article.getArticleId());
            article.setCommentList(comments);
        }
        return article;
    }

    public Article updateArticle(long articleId, Article updatedArticle) {
        final var existingArticle = articleRepository.getArticleById(new ArticleId(articleId));
        if (existingArticle != null) {
            existingArticle.setTitle(updatedArticle.getTitle());
            existingArticle.setTags(updatedArticle.getTags());
            articleRepository.updateArticle(existingArticle.getArticleId(), existingArticle);
        }
        return existingArticle;
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteArticle(new ArticleId(articleId));
    }

    public Article addArticle(Article newArticle) {
        final var articleId = new ArticleId(System.currentTimeMillis());
        newArticle.setArticleId(articleId);
        articleRepository.addArticle(newArticle);
        return newArticle;
    }

    public Comment addCommentToArticle(long articleId, Comment newComment) {
        final var article = articleRepository.getArticleById(new ArticleId(articleId));
        if (article != null) {
            final var commentId = new CommentId(System.currentTimeMillis());
            newComment.setCommentId(commentId);
            newComment.setArticleId(article.getArticleId());
            commentRepository.addComment(newComment);
        }
        return newComment;
    }

    public void deleteCommentFromArticle(long articleId, long commentId) {
        commentRepository.deleteComment(new CommentId(commentId));
    }
}
