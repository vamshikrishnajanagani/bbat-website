package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing news categories for organizing articles
 * Contains category information and relationships to news articles
 */
@Entity
@Table(name = "news_categories", indexes = {
    @Index(name = "idx_news_categories_slug", columnList = "slug", unique = true),
    @Index(name = "idx_news_categories_active", columnList = "is_active")
})
public class NewsCategory extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Slug is required")
    @Size(max = 100, message = "Slug must not exceed 100 characters")
    @Column(name = "slug", nullable = false, length = 100, unique = true)
    private String slug;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NewsArticle> articles = new ArrayList<>();

    // Constructors
    public NewsCategory() {}

    public NewsCategory(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<NewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articles = articles;
    }

    // Helper methods
    public void addArticle(NewsArticle article) {
        articles.add(article);
        article.setCategory(this);
    }

    public void removeArticle(NewsArticle article) {
        articles.remove(article);
        article.setCategory(null);
    }

    public long getPublishedArticleCount() {
        return articles.stream()
                .filter(article -> article.getIsPublished() != null && article.getIsPublished())
                .count();
    }

    @Override
    public String toString() {
        return "NewsCategory{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", isActive=" + isActive +
                ", articleCount=" + articles.size() +
                '}';
    }
}