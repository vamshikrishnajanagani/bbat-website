package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for District entity
 */
class DistrictTest {

    private District district;

    @BeforeEach
    void setUp() {
        district = new District();
        district.setName("Hyderabad");
        district.setCode("HYD");
        district.setHeadquarters("Hyderabad");
        district.setAreaSqKm(new BigDecimal("650.00"));
        district.setPopulation(6809970L);
        district.setLatitude(new BigDecimal("17.3850"));
        district.setLongitude(new BigDecimal("78.4867"));
        district.setIsActive(true);
    }

    @Test
    void testDistrictCreation() {
        assertNotNull(district);
        assertEquals("Hyderabad", district.getName());
        assertEquals("HYD", district.getCode());
        assertEquals("Hyderabad", district.getHeadquarters());
        assertTrue(district.getIsActive());
    }

    @Test
    void testDistrictWithConstructor() {
        District newDistrict = new District("Warangal", "WGL");
        assertEquals("Warangal", newDistrict.getName());
        assertEquals("WGL", newDistrict.getCode());
        assertTrue(newDistrict.getIsActive()); // Default value
    }

    @Test
    void testGeographicData() {
        assertEquals(new BigDecimal("650.00"), district.getAreaSqKm());
        assertEquals(6809970L, district.getPopulation());
        assertEquals(new BigDecimal("17.3850"), district.getLatitude());
        assertEquals(new BigDecimal("78.4867"), district.getLongitude());
    }

    @Test
    void testContactInformation() {
        district.setContactPerson("John Doe");
        district.setContactEmail("john@example.com");
        district.setContactPhone("+91-9876543210");

        assertEquals("John Doe", district.getContactPerson());
        assertEquals("john@example.com", district.getContactEmail());
        assertEquals("+91-9876543210", district.getContactPhone());
    }

    @Test
    void testPlayerRelationship() {
        Player player = new Player("Test Player");
        district.addPlayer(player);

        assertEquals(1, district.getPlayers().size());
        assertTrue(district.getPlayers().contains(player));
        assertEquals(district, player.getDistrict());
    }

    @Test
    void testRemovePlayer() {
        Player player = new Player("Test Player");
        district.addPlayer(player);
        district.removePlayer(player);

        assertEquals(0, district.getPlayers().size());
        assertFalse(district.getPlayers().contains(player));
        assertNull(player.getDistrict());
    }

    @Test
    void testTournamentRelationship() {
        Tournament tournament = new Tournament("Test Tournament", null, null);
        district.addTournament(tournament);

        assertEquals(1, district.getTournaments().size());
        assertTrue(district.getTournaments().contains(tournament));
        assertEquals(district, tournament.getDistrict());
    }

    @Test
    void testToString() {
        String toString = district.toString();
        assertTrue(toString.contains("Hyderabad"));
        assertTrue(toString.contains("HYD"));
        assertTrue(toString.contains("true"));
    }

    @Test
    void testEqualsAndHashCode() {
        District district1 = new District("Test", "TST");
        District district2 = new District("Test", "TST");

        // Without IDs, they should not be equal (BaseEntity equals implementation)
        assertNotEquals(district1, district2);

        // Test with same reference
        assertEquals(district1, district1);
    }
}