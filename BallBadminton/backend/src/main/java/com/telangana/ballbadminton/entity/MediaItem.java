package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity representing individual media items within galleries
 * Contains media file information, metadata, and display properties
 */
@Entity
@Table(name = "media_items", indexes = {
    @Index(name = "idx_media_items_gallery", columnList = "gallery_id"),
    @Index(name = "idx_media_items_type", columnList = "media_type"),
    @Index(name = "idx_media_items_active", columnList = "is_active"),
    @Index(name = "idx_media_items_sort", columnList = "sort_order")
})
public class MediaItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gallery_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_media_item_gallery"))
    private MediaGallery gallery;

    @Size(max = 200, message = "Media title must not exceed 200 characters")
    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "File URL is required")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters")
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", length = 20)
    private MediaType mediaType;

    @Column(name = "file_size")
    @Min(value = 0, message = "File size cannot be negative")
    private Long fileSize;

    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "sort_order", nullable = false)
    @Min(value = 0, message = "Sort order cannot be negative")
    private Integer sortOrder = 0;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Enums
    public enum MediaType {
        IMAGE("Image"),
        VIDEO("Video"),
        AUDIO("Audio");

        private final String displayName;

        MediaType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public MediaItem() {}

    public MediaItem(String fileUrl, MediaType mediaType) {
        this.fileUrl = fileUrl;
        this.mediaType = mediaType;
    }

    // Getters and Setters
    public MediaGallery getGallery() {
        return gallery;
    }

    public void setGallery(MediaGallery gallery) {
        this.gallery = gallery;
    }

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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Helper methods
    public String getDisplayUrl() {
        return thumbnailUrl != null && !thumbnailUrl.trim().isEmpty() ? thumbnailUrl : fileUrl;
    }

    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "Unknown";
        }
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public boolean isImage() {
        return mediaType == MediaType.IMAGE;
    }

    public boolean isVideo() {
        return mediaType == MediaType.VIDEO;
    }

    public boolean isAudio() {
        return mediaType == MediaType.AUDIO;
    }

    public String getFileExtension() {
        if (fileUrl == null || !fileUrl.contains(".")) {
            return "";
        }
        return fileUrl.substring(fileUrl.lastIndexOf(".") + 1).toLowerCase();
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "title='" + title + '\'' +
                ", mediaType=" + mediaType +
                ", fileSize=" + getFormattedFileSize() +
                ", sortOrder=" + sortOrder +
                ", isActive=" + isActive +
                '}';
    }
}