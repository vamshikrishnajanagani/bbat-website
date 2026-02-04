package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tournament entity
 */
class TournamentTest {

    private Tournament tournament;
    private District district;

    @BeforeEach
    void setUp() {
        district = new District("Hyderabad", "HYD");
        
        tournament = new Tournament();
        tournament.setName("State Championship 2024");
        tournament.setDescription("Annual state level championship");
        tournament.setStartDate(LocalDate.of(2024, 6, 15));
        tournament.setEndDate(LocalDate.of(2024, 6, 17));
        tournament.setVenue("Sports Complex, Hyderabad");
        tournament.setRegistrationStartDate(LocalDate.of(2024, 5, 1));
        tournament.setRegistrationEndDate(LocalDate.of(2024, 6, 10));
        tournament.setMaxParticipants(64);
        tournament.setEntryFee(new BigDecimal("500.00"));
        tournament.setPrizeMoney(new BigDecimal("50000.00"));
        tournament.setStatus(Tournament.Status.REGISTRATION_OPEN);
        tournament.setTournamentType(Tournament.TournamentType.SINGLES);
        tournament.setAgeCategory("Open");
        tournament.setGenderCategory("Men");
        tournament.setDistrict(district);
        tournament.setIsFeatured(true);
    }

    @Test
    void testTournamentCreation() {
        assertNotNull(tournament);
        assertEquals("State Championship 2024", tournament.getName());
        assertEquals("Annual state level championship", tournament.getDescription());
        assertEquals(LocalDate.of(2024, 6, 15), tournament.getStartDate());
        assertEquals(LocalDate.of(2024, 6, 17), tournament.getEndDate());
        assertEquals("Sports Complex, Hyderabad", tournament.getVenue());
        assertTrue(tournament.getIsFeatured());
    }

    @Test
    void testTournamentWithConstructor() {
        LocalDate start = LocalDate.of(2024, 7, 1);
        LocalDate end = LocalDate.of(2024, 7, 3);
        Tournament newTournament = new Tournament("Test Tournament", start, end);
        
        assertEquals("Test Tournament", newTournament.getName());
        assertEquals(start, newTournament.getStartDate());
        assertEquals(end, newTournament.getEndDate());
        assertEquals(Tournament.Status.UPCOMING, newTournament.getStatus()); // Default value
        assertFalse(newTournament.getIsFeatured()); // Default value
    }

    @Test
    void testRegistrationDates() {
        assertEquals(LocalDate.of(2024, 5, 1), tournament.getRegistrationStartDate());
        assertEquals(LocalDate.of(2024, 6, 10), tournament.getRegistrationEndDate());
    }

    @Test
    void testFinancialInformation() {
        assertEquals(new BigDecimal("500.00"), tournament.getEntryFee());
        assertEquals(new BigDecimal("50000.00"), tournament.getPrizeMoney());
    }

    @Test
    void testStatusEnum() {
        assertEquals("Upcoming", Tournament.Status.UPCOMING.getDisplayName());
        assertEquals("Registration Open", Tournament.Status.REGISTRATION_OPEN.getDisplayName());
        assertEquals("Registration Closed", Tournament.Status.REGISTRATION_CLOSED.getDisplayName());
        assertEquals("Ongoing", Tournament.Status.ONGOING.getDisplayName());
        assertEquals("Completed", Tournament.Status.COMPLETED.getDisplayName());
        assertEquals("Cancelled", Tournament.Status.CANCELLED.getDisplayName());
    }

    @Test
    void testTournamentTypeEnum() {
        assertEquals("Singles", Tournament.TournamentType.SINGLES.getDisplayName());
        assertEquals("Doubles", Tournament.TournamentType.DOUBLES.getDisplayName());
        assertEquals("Mixed", Tournament.TournamentType.MIXED.getDisplayName());
        assertEquals("Team", Tournament.TournamentType.TEAM.getDisplayName());
    }

    @Test
    void testStatusCheckers() {
        tournament.setStatus(Tournament.Status.UPCOMING);
        assertTrue(tournament.isUpcoming());
        assertFalse(tournament.isOngoing());
        assertFalse(tournament.isCompleted());
        assertFalse(tournament.isCancelled());

        tournament.setStatus(Tournament.Status.ONGOING);
        assertFalse(tournament.isUpcoming());
        assertTrue(tournament.isOngoing());
        assertFalse(tournament.isCompleted());
        assertFalse(tournament.isCancelled());

        tournament.setStatus(Tournament.Status.COMPLETED);
        assertFalse(tournament.isUpcoming());
        assertFalse(tournament.isOngoing());
        assertTrue(tournament.isCompleted());
        assertFalse(tournament.isCancelled());

        tournament.setStatus(Tournament.Status.CANCELLED);
        assertFalse(tournament.isUpcoming());
        assertFalse(tournament.isOngoing());
        assertFalse(tournament.isCompleted());
        assertTrue(tournament.isCancelled());
    }

    @Test
    void testRegistrationRelationship() {
        Player player = new Player("Test Player");
        TournamentRegistration registration = new TournamentRegistration(tournament, player);
        tournament.addRegistration(registration);

        assertEquals(1, tournament.getRegistrations().size());
        assertTrue(tournament.getRegistrations().contains(registration));
        assertEquals(tournament, registration.getTournament());
    }

    @Test
    void testRemoveRegistration() {
        Player player = new Player("Test Player");
        TournamentRegistration registration = new TournamentRegistration(tournament, player);
        tournament.addRegistration(registration);
        tournament.removeRegistration(registration);

        assertEquals(0, tournament.getRegistrations().size());
        assertFalse(tournament.getRegistrations().contains(registration));
        assertNull(registration.getTournament());
    }

    @Test
    void testCurrentRegistrationCount() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Player player3 = new Player("Player 3");

        TournamentRegistration reg1 = new TournamentRegistration(tournament, player1);
        reg1.setStatus(TournamentRegistration.RegistrationStatus.REGISTERED);
        
        TournamentRegistration reg2 = new TournamentRegistration(tournament, player2);
        reg2.setStatus(TournamentRegistration.RegistrationStatus.CONFIRMED);
        
        TournamentRegistration reg3 = new TournamentRegistration(tournament, player3);
        reg3.setStatus(TournamentRegistration.RegistrationStatus.WITHDRAWN);

        tournament.addRegistration(reg1);
        tournament.addRegistration(reg2);
        tournament.addRegistration(reg3);

        assertEquals(2, tournament.getCurrentRegistrationCount()); // Only REGISTERED and CONFIRMED count
    }

    @Test
    void testHasAvailableSlots() {
        tournament.setMaxParticipants(2);
        assertTrue(tournament.hasAvailableSlots());

        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        
        TournamentRegistration reg1 = new TournamentRegistration(tournament, player1);
        reg1.setStatus(TournamentRegistration.RegistrationStatus.REGISTERED);
        
        TournamentRegistration reg2 = new TournamentRegistration(tournament, player2);
        reg2.setStatus(TournamentRegistration.RegistrationStatus.CONFIRMED);

        tournament.addRegistration(reg1);
        tournament.addRegistration(reg2);

        assertFalse(tournament.hasAvailableSlots());

        // Test with null max participants (unlimited)
        tournament.setMaxParticipants(null);
        assertTrue(tournament.hasAvailableSlots());
    }

    @Test
    void testGetDurationInDays() {
        assertEquals(3, tournament.getDurationInDays()); // June 15-17 = 3 days

        tournament.setStartDate(LocalDate.of(2024, 6, 15));
        tournament.setEndDate(LocalDate.of(2024, 6, 15));
        assertEquals(1, tournament.getDurationInDays()); // Same day = 1 day

        tournament.setStartDate(null);
        assertEquals(0, tournament.getDurationInDays());
    }

    @Test
    void testDistrictRelationship() {
        assertEquals(district, tournament.getDistrict());
        assertEquals("Hyderabad", tournament.getDistrict().getName());
    }

    @Test
    void testCategorization() {
        assertEquals("Open", tournament.getAgeCategory());
        assertEquals("Men", tournament.getGenderCategory());
        assertEquals(Tournament.TournamentType.SINGLES, tournament.getTournamentType());
    }

    @Test
    void testDefaultValues() {
        Tournament newTournament = new Tournament();
        assertEquals(Tournament.Status.UPCOMING, newTournament.getStatus());
        assertEquals(BigDecimal.ZERO, newTournament.getEntryFee());
        assertEquals(BigDecimal.ZERO, newTournament.getPrizeMoney());
        assertFalse(newTournament.getIsFeatured());
        assertTrue(newTournament.getRegistrations().isEmpty());
    }

    @Test
    void testToString() {
        String toString = tournament.toString();
        assertTrue(toString.contains("State Championship 2024"));
        assertTrue(toString.contains("2024-06-15"));
        assertTrue(toString.contains("2024-06-17"));
        assertTrue(toString.contains("Sports Complex, Hyderabad"));
        assertTrue(toString.contains("REGISTRATION_OPEN"));
        assertTrue(toString.contains("Hyderabad"));
    }
}