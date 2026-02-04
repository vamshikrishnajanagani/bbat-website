package com.telangana.ballbadminton.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Contact form request DTO for contacting members
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class ContactFormRequest {

    private UUID memberId;

    @NotBlank(message = "Sender name is required")
    @Size(max = 100, message = "Sender name must not exceed 100 characters")
    private String senderName;

    @NotBlank(message = "Sender email is required")
    @Email(message = "Sender email should be valid")
    @Size(max = 100, message = "Sender email must not exceed 100 characters")
    private String senderEmail;

    @Size(max = 20, message = "Sender phone must not exceed 20 characters")
    private String senderPhone;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;

    // Constructors
    public ContactFormRequest() {}

    public ContactFormRequest(String senderName, String senderEmail, String subject, String message) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.message = message;
    }

    // Getters and Setters
    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ContactFormRequest{" +
                "memberId=" + memberId +
                ", senderName='" + senderName + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}