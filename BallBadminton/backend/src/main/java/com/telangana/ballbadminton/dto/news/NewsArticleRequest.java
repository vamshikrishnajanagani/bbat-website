package com.telangana.ballbadminton.dto.news;

import com.telangana.ballbadminton.entity.NewsArticle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for NewsArticle operations
 */
public class NewsArticleRequest {

    @NotBlank(message = "Article title is required")
    @Size(max = 300, message = "Article title must not exceed 300 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(max = 300, message = "Slug must not exceed 300 characters")
    private String slug;

    private String summary;

    @NotBlank(message = "Article content is required")
    private String content;

    @Size(max = 500, message = "Featured image URL must not exceed 500 characters")
    private String featuredImageUrl;

    @Size(max = 100, message = "Author name must not exceed 100 characters")
    private String author;

    private LocalDateTime publishedAt;
    private Boolean isPublished = false;
    private Boolean isFeatured = false;
    private NewsArticle.Language language = NewsArticle.Language.ENGLISH;
    private UUID categoryId;

    // Constructors
    public NewsArticleRequest() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public NewsArticle.Language getLanguage() {
        return language;
    }

    public void setLanguage(NewsArticle.Language language) {
        this.language = language;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}