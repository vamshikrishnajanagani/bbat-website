package com.telangana.ballbadminton.dto.news;

import com.telangana.ballbadminton.entity.NewsArticle;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for NewsArticle operations
 */
public class NewsArticleResponse {

    private UUID id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String featuredImageUrl;
    private String author;
    private LocalDateTime publishedAt;
    private Boolean isPublished;
    private Boolean isFeatured;
    private Integer viewCount;
    private NewsArticle.Language language;
    private NewsCategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public NewsArticleResponse() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public NewsArticle.Language getLanguage() {
        return language;
    }

    public void setLanguage(NewsArticle.Language language) {
        this.language = language;
    }

    public NewsCategoryResponse getCategory() {
        return category;
    }

    public void setCategory(NewsCategoryResponse category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}