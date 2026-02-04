package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entity representing news articles and announcements
 * Contains article content, metadata, and publication information
 */
@Entity
@Table(name = "news_articles", indexes = {
    @Index(name = "idx_news_published", columnList = "is_published"),
    @Index(name = "idx_news_featured", columnList = "is_featured"),
    @Index(name = "idx_news_category", columnList = "category_id"),
    @Index(name = "idx_news_published_at", columnList = "published_at"),
    @Index(name = "idx_news_slug", columnList = "slug", unique = true)
})
public class NewsArticle extends BaseEntity {

    @NotBlank(message = "Article title is required")
    @Size(max = 300, message = "Article title must not exceed 300 characters")
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(max = 300, message = "Slug must not exceed 300 characters")
    @Column(name = "slug", nullable = false, length = 300, unique = true)
    private String slug;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @NotBlank(message = "Article content is required")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Size(max = 500, message = "Featured image URL must not exceed 500 characters")
    @Column(name = "featured_image_url", length = 500)
    private String featuredImageUrl;

    @Size(max = 100, message = "Author name must not exceed 100 characters")
    @Column(name = "author", length = 100)
    private String author;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "scheduled_publication_date")
    private LocalDateTime scheduledPublicationDate;

    @NotNull
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @NotNull
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", length = 5, nullable = false)
    private Language language = Language.ENGLISH;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
                foreignKey = @ForeignKey(name = "fk_news_article_category"))
    private NewsCategory category;

    // Enums
    public enum Language {
        ENGLISH("en"),
        TELUGU("te");

        private final String code;

        Language(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    // Constructors
    public NewsArticle() {}

    public NewsArticle(String title, String content) {
        this.title = title;
        this.content = content;
    }

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

    public LocalDateTime getScheduledPublicationDate() {
        return scheduledPublicationDate;
    }

    public void setScheduledPublicationDate(LocalDateTime scheduledPublicationDate) {
        this.scheduledPublicationDate = scheduledPublicationDate;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
        if (isPublished && publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public void setCategory(NewsCategory category) {
        this.category = category;
    }

    // Helper methods
    public void publish() {
        this.isPublished = true;
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void unpublish() {
        this.isPublished = false;
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public boolean isRecentlyPublished() {
        if (publishedAt == null) {
            return false;
        }
        return publishedAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    public String getExcerpt(int maxLength) {
        if (summary != null && !summary.trim().isEmpty()) {
            return summary.length() > maxLength ? 
                   summary.substring(0, maxLength) + "..." : summary;
        }
        
        if (content != null && !content.trim().isEmpty()) {
            String plainContent = content.replaceAll("<[^>]*>", ""); // Remove HTML tags
            return plainContent.length() > maxLength ? 
                   plainContent.substring(0, maxLength) + "..." : plainContent;
        }
        
        return "";
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", author='" + author + '\'' +
                ", isPublished=" + isPublished +
                ", isFeatured=" + isFeatured +
                ", publishedAt=" + publishedAt +
                ", viewCount=" + viewCount +
                '}';
    }
}