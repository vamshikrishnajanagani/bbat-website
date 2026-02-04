package com.telangana.ballbadminton.service;

/**
 * Email service interface for sending various types of emails
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public interface EmailService {

    /**
     * Send contact form email to a specific member
     */
    void sendContactFormEmail(String memberEmail, String memberName, String senderName, 
                             String senderEmail, String subject, String message);

    /**
     * Send general contact form email to admin
     */
    void sendGeneralContactFormEmail(String senderName, String senderEmail, String subject, String message);

    /**
     * Send notification email
     */
    void sendNotificationEmail(String toEmail, String subject, String message);

    /**
     * Send simple email
     */
    void sendEmail(String toEmail, String subject, String message);
}