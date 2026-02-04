package com.telangana.ballbadminton.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * File upload service interface for handling file uploads
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public interface FileUploadService {

    /**
     * Upload member photo and return the URL
     */
    String uploadMemberPhoto(MultipartFile file);

    /**
     * Upload general file and return the URL
     */
    String uploadFile(MultipartFile file, String directory);

    /**
     * Delete file by URL
     */
    void deleteFile(String fileUrl);

    /**
     * Upload media file (image, video, audio) and return the URL
     */
    String uploadMediaFile(MultipartFile file);

    /**
     * Validate file type and size
     */
    boolean isValidImageFile(MultipartFile file);
}