package org.example.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Set;
import java.util.Objects;


public class Article {
    private final ArticleId articleId;
    private String title;
    private Set<String> tags;
    private List<Comment> commentList;
    private boolean trending;

    public Article(ArticleId articleId, String title, Set<String> tags, List<Comment> commentList) {
        this.articleId = articleId;
        this.title = title;
        this.tags = tags;
        this.commentList = commentList;
        this.trending = isTrending();
    }

    public Article withTitle(String title) {
        return new Article(this.articleId, title, this.tags, this.commentList);
    }

    public ArticleId getArticleId() {
        return articleId;
    }

    public List<Comment> getCommentList() {
        return commentList;
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

    public boolean isTrending() {
        return trending;
    }

    public void setTrending(boolean trending) {
        this.trending = trending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Article article)) {
            return false;
        }
        return articleId.equals(article.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId);
    }

    public static class ArticleId {
        private final long id;

        @JsonCreator
        public ArticleId(long id) {
            this.id = id;
        }

        @JsonValue
        public long value() {
            return id;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }


        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ArticleId articleId = (ArticleId) obj;
            return id == articleId.id;
        }
    }
}
