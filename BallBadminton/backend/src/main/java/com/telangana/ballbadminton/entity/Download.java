package com.telangana.ballbadminton.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity representing downloadable resources and documents
 * Contains file information, categorization, and download tracking
 */
@Entity
@Table(name = "downloads", indexes = {
    @Index(name = "idx_downloads_category", columnList = "category"),
    @Index(name = "idx_downloads_public", columnList = "is_public"),
    @Index(name = "idx_downloads_active", columnList = "is_active")
})
public class Download extends BaseEntity {

    @NotBlank(message = "Download title is required")
    @Size(max = 200, message = "Download title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "File URL is required")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size")
    @Min(value = 0, message = "File size cannot be negative")
    private Long fileSize;

    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "download_count", nullable = false)
    @Min(value = 0, message = "Download count cannot be negative")
    private Integer downloadCount = 0;

    @NotNull
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public Download() {}

    public Download(String title, String fileName, String fileUrl) {
        this.title = title;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Helper methods
    public void incrementDownloadCount() {
        this.downloadCount = (this.downloadCount == null ? 0 : this.downloadCount) + 1;
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

    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public boolean isPdf() {
        return "pdf".equalsIgnoreCase(getFileExtension()) || 
               (mimeType != null && mimeType.contains("pdf"));
    }

    public boolean isDocument() {
        String ext = getFileExtension();
        return "doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext) ||
               "txt".equalsIgnoreCase(ext) || "rtf".equalsIgnoreCase(ext) ||
               (mimeType != null && (mimeType.contains("document") || mimeType.contains("text")));
    }

    public boolean isSpreadsheet() {
        String ext = getFileExtension();
        return "xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext) ||
               "csv".equalsIgnoreCase(ext) ||
               (mimeType != null && mimeType.contains("spreadsheet"));
    }

    public boolean isPresentation() {
        String ext = getFileExtension();
        return "ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext) ||
               (mimeType != null && mimeType.contains("presentation"));
    }

    public boolean isImage() {
        String ext = getFileExtension();
        return "jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext) ||
               "png".equalsIgnoreCase(ext) || "gif".equalsIgnoreCase(ext) ||
               "bmp".equalsIgnoreCase(ext) || "svg".equalsIgnoreCase(ext) ||
               (mimeType != null && mimeType.startsWith("image/"));
    }

    public String getFileTypeIcon() {
        if (isPdf()) return "fa-file-pdf";
        if (isDocument()) return "fa-file-word";
        if (isSpreadsheet()) return "fa-file-excel";
        if (isPresentation()) return "fa-file-powerpoint";
        if (isImage()) return "fa-file-image";
        return "fa-file";
    }

    @Override
    public String toString() {
        return "Download{" +
                "title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", category='" + category + '\'' +
                ", fileSize=" + getFormattedFileSize() +
                ", downloadCount=" + downloadCount +
                ", isPublic=" + isPublic +
                ", isActive=" + isActive +
                '}';
    }
}