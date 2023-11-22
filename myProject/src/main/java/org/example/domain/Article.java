package org.example.domain;

import java.util.List;
import java.util.Set;
import java.util.Objects;


public class Article {
    private ArticleId articleId;
    private String title;
    private Set<String> tags;
    private List<Comment> commentList;

    public Article(ArticleId articleId, String title, Set<String> tags, List<Comment> commentList) {
        this.articleId = articleId;
        this.title = title;
        this.tags = tags;
        this.commentList = commentList;
    }

    public ArticleId getArticleId() {
        return articleId;
    }

    public void setArticleId(ArticleId articleId) {
        this.articleId = articleId;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public static class ArticleId {
        private final long id;

        public ArticleId(long id) {
            this.id = id;
        }

        public long value() {
            return id;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Article article = (Article) o;
        return articleId == article.articleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId);
    }
}
