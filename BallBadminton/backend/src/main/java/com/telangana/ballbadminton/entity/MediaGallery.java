package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing media galleries for organizing photos and videos
 * Contains gallery information and relationships to media items
 */
@Entity
@Table(name = "media_galleries", indexes = {
    @Index(name = "idx_media_galleries_featured", columnList = "is_featured"),
    @Index(name = "idx_media_galleries_public", columnList = "is_public")
})
public class MediaGallery extends BaseEntity {

    @NotBlank(message = "Gallery title is required")
    @Size(max = 200, message = "Gallery title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "gallery_type", length = 20)
    private GalleryType galleryType;

    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @NotNull
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @NotNull
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    // Relationships
    @OneToMany(mappedBy = "gallery", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MediaItem> mediaItems = new ArrayList<>();

    // Enums
    public enum GalleryType {
        PHOTO("Photo"),
        VIDEO("Video"),
        MIXED("Mixed");

        private final String displayName;

        GalleryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public MediaGallery() {}

    public MediaGallery(String title, GalleryType galleryType) {
        this.title = title;
        this.galleryType = galleryType;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GalleryType getGalleryType() {
        return galleryType;
    }

    public void setGalleryType(GalleryType galleryType) {
        this.galleryType = galleryType;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<MediaItem> getMediaItems() {
        return mediaItems;
    }

    public void setMediaItems(List<MediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }

    // Helper methods
    public void addMediaItem(MediaItem mediaItem) {
        mediaItems.add(mediaItem);
        mediaItem.setGallery(this);
    }

    public void removeMediaItem(MediaItem mediaItem) {
        mediaItems.remove(mediaItem);
        mediaItem.setGallery(null);
    }

    public long getActiveMediaItemCount() {
        return mediaItems.stream()
                .filter(item -> item.getIsActive() != null && item.getIsActive())
                .count();
    }

    public List<MediaItem> getActiveMediaItems() {
        return mediaItems.stream()
                .filter(item -> item.getIsActive() != null && item.getIsActive())
                .sorted((a, b) -> Integer.compare(
                    a.getSortOrder() != null ? a.getSortOrder() : 0,
                    b.getSortOrder() != null ? b.getSortOrder() : 0))
                .toList();
    }

    public MediaItem getCoverMediaItem() {
        if (coverImageUrl != null && !coverImageUrl.trim().isEmpty()) {
            return null; // Use the explicit cover image URL
        }
        
        return mediaItems.stream()
                .filter(item -> item.getIsActive() != null && item.getIsActive())
                .filter(item -> item.getMediaType() == MediaItem.MediaType.IMAGE)
                .min((a, b) -> Integer.compare(
                    a.getSortOrder() != null ? a.getSortOrder() : 0,
                    b.getSortOrder() != null ? b.getSortOrder() : 0))
                .orElse(null);
    }

    public boolean isEmpty() {
        return getActiveMediaItemCount() == 0;
    }

    @Override
    public String toString() {
        return "MediaGallery{" +
                "title='" + title + '\'' +
                ", galleryType=" + galleryType +
                ", isFeatured=" + isFeatured +
                ", isPublic=" + isPublic +
                ", mediaItemCount=" + mediaItems.size() +
                '}';
    }
}