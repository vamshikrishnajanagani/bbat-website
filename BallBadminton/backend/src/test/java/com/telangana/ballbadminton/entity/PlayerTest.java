package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Player entity
 */
class PlayerTest {

    private Player player;
    private District district;

    @BeforeEach
    void setUp() {
        district = new District("Hyderabad", "HYD");
        
        player = new Player();
        player.setName("Rajesh Kumar");
        player.setDateOfBirth(LocalDate.of(1995, 5, 15));
        player.setGender(Player.Gender.MALE);
        player.setCategory(Player.Category.MEN);
        player.setContactEmail("rajesh@example.com");
        player.setContactPhone("+91-9876543210");
        player.setAddress("123 Main Street, Hyderabad");
        player.setDistrict(district);
        player.setIsProminent(true);
        player.setIsActive(true);
    }

    @Test
    void testPlayerCreation() {
        assertNotNull(player);
        assertEquals("Rajesh Kumar", player.getName());
        assertEquals(Player.Gender.MALE, player.getGender());
        assertEquals(Player.Category.MEN, player.getCategory());
        assertTrue(player.getIsProminent());
        assertTrue(player.getIsActive());
    }

    @Test
    void testPlayerWithConstructor() {
        Player newPlayer = new Player("Test Player");
        assertEquals("Test Player", newPlayer.getName());
        assertFalse(newPlayer.getIsProminent()); // Default value
        assertTrue(newPlayer.getIsActive()); // Default value
    }

    @Test
    void testContactInformation() {
        assertEquals("rajesh@example.com", player.getContactEmail());
        assertEquals("+91-9876543210", player.getContactPhone());
        assertEquals("123 Main Street, Hyderabad", player.getAddress());
    }

    @Test
    void testDistrictRelationship() {
        assertEquals(district, player.getDistrict());
        assertEquals("Hyderabad", player.getDistrict().getName());
    }

    @Test
    void testAgeCalculation() {
        int expectedAge = LocalDate.now().getYear() - 1995;
        assertEquals(expectedAge, player.getAge());

        // Test with null date of birth
        player.setDateOfBirth(null);
        assertEquals(0, player.getAge());
    }

    @Test
    void testGenderEnum() {
        assertEquals("Male", Player.Gender.MALE.getDisplayName());
        assertEquals("Female", Player.Gender.FEMALE.getDisplayName());
        assertEquals("Other", Player.Gender.OTHER.getDisplayName());
    }

    @Test
    void testCategoryEnum() {
        assertEquals("Men", Player.Category.MEN.getDisplayName());
        assertEquals("Women", Player.Category.WOMEN.getDisplayName());
        assertEquals("Junior", Player.Category.JUNIOR.getDisplayName());
        assertEquals("Senior", Player.Category.SENIOR.getDisplayName());
        assertEquals("Veterans", Player.Category.VETERANS.getDisplayName());
    }

    @Test
    void testAchievementRelationship() {
        Achievement achievement = new Achievement("State Champion", player);
        player.addAchievement(achievement);

        assertEquals(1, player.getAchievements().size());
        assertTrue(player.getAchievements().contains(achievement));
        assertEquals(player, achievement.getPlayer());
    }

    @Test
    void testRemoveAchievement() {
        Achievement achievement = new Achievement("State Champion", player);
        player.addAchievement(achievement);
        player.removeAchievement(achievement);

        assertEquals(0, player.getAchievements().size());
        assertFalse(player.getAchievements().contains(achievement));
        assertNull(achievement.getPlayer());
    }

    @Test
    void testStatisticsRelationship() {
        PlayerStatistics stats = new PlayerStatistics(player);
        player.setStatistics(stats);

        assertEquals(stats, player.getStatistics());
        assertEquals(player, stats.getPlayer());
    }

    @Test
    void testTournamentRegistrationRelationship() {
        Tournament tournament = new Tournament("Test Tournament", LocalDate.now(), LocalDate.now().plusDays(1));
        TournamentRegistration registration = new TournamentRegistration(tournament, player);
        player.addTournamentRegistration(registration);

        assertEquals(1, player.getTournamentRegistrations().size());
        assertTrue(player.getTournamentRegistrations().contains(registration));
        assertEquals(player, registration.getPlayer());
    }

    @Test
    void testProfilePhoto() {
        player.setProfilePhotoUrl("https://example.com/photo.jpg");
        assertEquals("https://example.com/photo.jpg", player.getProfilePhotoUrl());
    }

    @Test
    void testDefaultValues() {
        Player newPlayer = new Player();
        assertFalse(newPlayer.getIsProminent());
        assertTrue(newPlayer.getIsActive());
        assertTrue(newPlayer.getAchievements().isEmpty());
        assertTrue(newPlayer.getTournamentRegistrations().isEmpty());
    }

    @Test
    void testToString() {
        String toString = player.toString();
        assertTrue(toString.contains("Rajesh Kumar"));
        assertTrue(toString.contains("MEN"));
        assertTrue(toString.contains("Hyderabad"));
        assertTrue(toString.contains("true"));
    }
}