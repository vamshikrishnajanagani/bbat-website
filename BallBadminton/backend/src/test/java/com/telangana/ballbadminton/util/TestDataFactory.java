package com.telangana.ballbadminton.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test data factory for creating mock objects in tests
 * 
 * This utility class provides factory methods for creating test data objects
 * with sensible defaults that can be overridden as needed for specific tests.
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class TestDataFactory {

    // Default test data constants
    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String DEFAULT_PHONE = "+91-9876543210";
    public static final String DEFAULT_PASSWORD = "TestPassword123!";
    public static final String DEFAULT_NAME = "Test User";
    
    /**
     * Creates a test user with default values
     */
    public static TestUser createTestUser() {
        return TestUser.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email(DEFAULT_EMAIL)
                .passwordHash("$2a$04$YNDZmvJgYgladeGOEP3nH.WdJYNjQOqOkGnBp2xkjvgOGGfTWAC4G") // "password"
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a test member with default values
     */
    public static TestMember createTestMember() {
        return TestMember.builder().build();
    }

    /**
     * Creates a test player with default values
     */
    public static TestPlayer createTestPlayer() {
        return TestPlayer.builder().build();
    }

    /**
     * Creates a test tournament with default values
     */
    public static TestTournament createTestTournament() {
        return TestTournament.builder().build();
    }

    /**
     * Creates a test district with default values
     */
    public static TestDistrict createTestDistrict() {
        return TestDistrict.builder().build();
    }

    /**
     * Creates a test news article with default values
     */
    public static TestNewsArticle createTestNewsArticle() {
        return TestNewsArticle.builder().build();
    }

    // Builder classes for test objects
    public static class TestUser {
        private UUID id;
        private String username;
        private String email;
        private String passwordHash;
        private String firstName;
        private String lastName;
        private Boolean isActive;
        private Boolean isVerified;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static TestUserBuilder builder() {
            return new TestUserBuilder();
        }

        // Getters and setters
        public UUID getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPasswordHash() { return passwordHash; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public Boolean getIsActive() { return isActive; }
        public Boolean getIsVerified() { return isVerified; }
        public LocalDateTime getLastLoginAt() { return lastLoginAt; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }

        public static class TestUserBuilder {
            private TestUser user = new TestUser();

            public TestUserBuilder id(UUID id) { user.id = id; return this; }
            public TestUserBuilder username(String username) { user.username = username; return this; }
            public TestUserBuilder email(String email) { user.email = email; return this; }
            public TestUserBuilder passwordHash(String passwordHash) { user.passwordHash = passwordHash; return this; }
            public TestUserBuilder firstName(String firstName) { user.firstName = firstName; return this; }
            public TestUserBuilder lastName(String lastName) { user.lastName = lastName; return this; }
            public TestUserBuilder isActive(Boolean isActive) { user.isActive = isActive; return this; }
            public TestUserBuilder isVerified(Boolean isVerified) { user.isVerified = isVerified; return this; }
            public TestUserBuilder lastLoginAt(LocalDateTime lastLoginAt) { user.lastLoginAt = lastLoginAt; return this; }
            public TestUserBuilder createdAt(LocalDateTime createdAt) { user.createdAt = createdAt; return this; }
            public TestUserBuilder updatedAt(LocalDateTime updatedAt) { user.updatedAt = updatedAt; return this; }

            public TestUser build() { return user; }
        }
    }

    // Simplified builder classes for other test objects
    public static class TestMember {
        public static TestMemberBuilder builder() { return new TestMemberBuilder(); }
        public static class TestMemberBuilder {
            public TestMember build() { return new TestMember(); }
        }
    }

    public static class TestPlayer {
        public static TestPlayerBuilder builder() { return new TestPlayerBuilder(); }
        public static class TestPlayerBuilder {
            public TestPlayer build() { return new TestPlayer(); }
        }
    }

    public static class TestTournament {
        public static TestTournamentBuilder builder() { return new TestTournamentBuilder(); }
        public static class TestTournamentBuilder {
            public TestTournament build() { return new TestTournament(); }
        }
    }

    public static class TestDistrict {
        public static TestDistrictBuilder builder() { return new TestDistrictBuilder(); }
        public static class TestDistrictBuilder {
            public TestDistrict build() { return new TestDistrict(); }
        }
    }

    public static class TestNewsArticle {
        public static TestNewsArticleBuilder builder() { return new TestNewsArticleBuilder(); }
        public static class TestNewsArticleBuilder {
            public TestNewsArticle build() { return new TestNewsArticle(); }
        }
    }
}