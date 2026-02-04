package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for entity relationships and constraints
 * Validates the enhanced JPA relationships and validation constraints
 */
class EntityRelationshipsTest {

    @Test
    @DisplayName("Player-District relationship should work correctly")
    void testPlayerDistrictRelationship() {
        // Given
        District district = new District("Hyderabad", "HYD");
        district.setHeadquarters("Hyderabad");
        district.setIsActive(true);

        Player player = new Player("Test Player");
        player.setGender(Player.Gender.MALE);
        player.setCategory(Player.Category.MEN);
        player.setIsActive(true);

        // When
        district.addPlayer(player);

        // Then
        assertEquals(district, player.getDistrict());
        assertTrue(district.getPlayers().contains(player));
        assertEquals(1, district.getPlayers().size());
    }

    @Test
    @DisplayName("Player-Achievement relationship should work correctly")
    void testPlayerAchievementRelationship() {
        // Given
        Player player = new Player("Test Player");
        Achievement achievement = new Achievement("Test Achievement", player);
        achievement.setLevel(Achievement.Level.STATE);
        achievement.setPosition(1);
        achievement.setIsVerified(true);

        // When
        player.addAchievement(achievement);

        // Then
        assertEquals(player, achievement.getPlayer());
        assertTrue(player.getAchievements().contains(achievement));
        assertEquals(1, player.getAchievements().size());
    }

    @Test
    @DisplayName("Player-PlayerStatistics relationship should work correctly")
    void testPlayerStatisticsRelationship() {
        // Given
        Player player = new Player("Test Player");
        PlayerStatistics statistics = new PlayerStatistics(player);
        statistics.setMatchesPlayed(10);
        statistics.setMatchesWon(7);
        statistics.setTournamentsParticipated(3);
        statistics.setTournamentsWon(1);

        // When
        player.setStatistics(statistics);

        // Then
        assertEquals(player, statistics.getPlayer());
        assertEquals(statistics, player.getStatistics());
        assertEquals(0, BigDecimal.valueOf(70.00).compareTo(statistics.getWinPercentage()));
    }

    @Test
    @DisplayName("Tournament-District relationship should work correctly")
    void testTournamentDistrictRelationship() {
        // Given
        District district = new District("Warangal", "WGL");
        Tournament tournament = new Tournament("Test Tournament", 
                                             LocalDate.now().plusDays(30), 
                                             LocalDate.now().plusDays(32));
        tournament.setVenue("Test Venue");
        tournament.setStatus(Tournament.Status.UPCOMING);

        // When
        district.addTournament(tournament);

        // Then
        assertEquals(district, tournament.getDistrict());
        assertTrue(district.getTournaments().contains(tournament));
        assertEquals(1, district.getTournaments().size());
    }

    @Test
    @DisplayName("Tournament-TournamentRegistration-Player relationship should work correctly")
    void testTournamentRegistrationRelationship() {
        // Given
        Tournament tournament = new Tournament("Test Tournament", 
                                             LocalDate.now().plusDays(30), 
                                             LocalDate.now().plusDays(32));
        Player player = new Player("Test Player");
        TournamentRegistration registration = new TournamentRegistration(tournament, player);
        registration.setPaymentAmount(BigDecimal.valueOf(500.00));
        registration.setStatus(TournamentRegistration.RegistrationStatus.REGISTERED);

        // When
        tournament.addRegistration(registration);
        player.addTournamentRegistration(registration);

        // Then
        assertEquals(tournament, registration.getTournament());
        assertEquals(player, registration.getPlayer());
        assertTrue(tournament.getRegistrations().contains(registration));
        assertTrue(player.getTournamentRegistrations().contains(registration));
    }

    @Test
    @DisplayName("NewsCategory-NewsArticle relationship should work correctly")
    void testNewsCategoryArticleRelationship() {
        // Given
        NewsCategory category = new NewsCategory("Test Category", "test-category");
        category.setDescription("Test category description");
        category.setIsActive(true);

        NewsArticle article = new NewsArticle("Test Article", "Test content");
        article.setSlug("test-article");
        article.setAuthor("Test Author");
        article.setIsPublished(true);

        // When
        category.addArticle(article);

        // Then
        assertEquals(category, article.getCategory());
        assertTrue(category.getArticles().contains(article));
        assertEquals(1, category.getArticles().size());
    }

    @Test
    @DisplayName("MediaGallery-MediaItem relationship should work correctly")
    void testMediaGalleryItemRelationship() {
        // Given
        MediaGallery gallery = new MediaGallery("Test Gallery", MediaGallery.GalleryType.PHOTO);
        gallery.setDescription("Test gallery description");
        gallery.setIsPublic(true);

        MediaItem item = new MediaItem("http://example.com/image.jpg", MediaItem.MediaType.IMAGE);
        item.setTitle("Test Image");
        item.setSortOrder(1);
        item.setIsActive(true);

        // When
        gallery.addMediaItem(item);

        // Then
        assertEquals(gallery, item.getGallery());
        assertTrue(gallery.getMediaItems().contains(item));
        assertEquals(1, gallery.getMediaItems().size());
    }

    @Test
    @DisplayName("Achievement position validation should work correctly")
    void testAchievementPositionText() {
        // Given
        Achievement achievement1 = new Achievement("First Place", null);
        achievement1.setPosition(1);

        Achievement achievement2 = new Achievement("Second Place", null);
        achievement2.setPosition(2);

        Achievement achievement3 = new Achievement("Third Place", null);
        achievement3.setPosition(3);

        Achievement achievement4 = new Achievement("Fourth Place", null);
        achievement4.setPosition(4);

        // Then
        assertEquals("1st Place", achievement1.getPositionText());
        assertEquals("2nd Place", achievement2.getPositionText());
        assertEquals("3rd Place", achievement3.getPositionText());
        assertEquals("4th Place", achievement4.getPositionText());
    }

    @Test
    @DisplayName("Tournament helper methods should work correctly")
    void testTournamentHelperMethods() {
        // Given
        Tournament tournament = new Tournament("Test Tournament", 
                                             LocalDate.now().plusDays(30), 
                                             LocalDate.now().plusDays(32));
        tournament.setMaxParticipants(10);
        tournament.setStatus(Tournament.Status.REGISTRATION_OPEN);
        tournament.setRegistrationStartDate(LocalDate.now().minusDays(5));
        tournament.setRegistrationEndDate(LocalDate.now().plusDays(10));

        // Then
        assertTrue(tournament.isRegistrationOpen());
        assertTrue(tournament.isUpcoming());
        assertFalse(tournament.isOngoing());
        assertFalse(tournament.isCompleted());
        assertTrue(tournament.hasAvailableSlots());
        assertEquals(3, tournament.getDurationInDays());
    }

    @Test
    @DisplayName("PlayerStatistics calculations should work correctly")
    void testPlayerStatisticsCalculations() {
        // Given
        Player player = new Player("Test Player");
        PlayerStatistics stats = new PlayerStatistics(player);

        // When
        stats.addMatchResult(true);  // Win
        stats.addMatchResult(true);  // Win
        stats.addMatchResult(false); // Loss
        stats.addTournamentParticipation(true); // Tournament win

        // Then
        assertEquals(3, stats.getMatchesPlayed());
        assertEquals(2, stats.getMatchesWon());
        assertEquals(0, BigDecimal.valueOf(66.67).compareTo(stats.getWinPercentage()));
        assertEquals(1, stats.getTournamentsParticipated());
        assertEquals(1, stats.getTournamentsWon());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(stats.getTournamentWinPercentage()));
    }
}