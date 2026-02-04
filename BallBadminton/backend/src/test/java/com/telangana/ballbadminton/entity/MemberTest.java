package com.telangana.ballbadminton.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Member entity
 */
class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setName("John Doe");
        member.setPosition("President");
        member.setEmail("john.doe@example.com");
        member.setPhone("+91-9876543210");
        member.setBiography("Experienced sports administrator");
        member.setHierarchyLevel(1);
        member.setTenureStartDate(LocalDate.of(2023, 1, 1));
        member.setTenureEndDate(LocalDate.now().plusYears(1)); // Future end date
        member.setIsActive(true);
        member.setIsProminent(true);
    }

    @Test
    void testMemberCreation() {
        assertNotNull(member);
        assertEquals("John Doe", member.getName());
        assertEquals("President", member.getPosition());
        assertEquals("john.doe@example.com", member.getEmail());
        assertTrue(member.getIsActive());
        assertTrue(member.getIsProminent());
    }

    @Test
    void testMemberWithConstructor() {
        Member newMember = new Member("Jane Smith", "Secretary");
        assertEquals("Jane Smith", newMember.getName());
        assertEquals("Secretary", newMember.getPosition());
        assertTrue(newMember.getIsActive()); // Default value
        assertFalse(newMember.getIsProminent()); // Default value
    }

    @Test
    void testContactInformation() {
        assertEquals("+91-9876543210", member.getPhone());
        assertEquals("john.doe@example.com", member.getEmail());
    }

    @Test
    void testHierarchyAndTenure() {
        assertEquals(1, member.getHierarchyLevel());
        assertEquals(LocalDate.of(2023, 1, 1), member.getTenureStartDate());
        assertTrue(member.getTenureEndDate().isAfter(LocalDate.now())); // Future end date
    }

    @Test
    void testIsCurrentlyServing() {
        // Member with current tenure (future end date)
        assertTrue(member.isCurrentlyServing());

        // Member with expired tenure
        member.setTenureEndDate(LocalDate.now().minusDays(1));
        assertFalse(member.isCurrentlyServing());

        // Member with future start date
        member.setTenureStartDate(LocalDate.now().plusDays(1));
        member.setTenureEndDate(LocalDate.now().plusYears(1));
        assertFalse(member.isCurrentlyServing());

        // Inactive member
        member.setTenureStartDate(LocalDate.of(2023, 1, 1));
        member.setTenureEndDate(LocalDate.now().plusYears(1));
        member.setIsActive(false);
        assertFalse(member.isCurrentlyServing());
    }

    @Test
    void testHasTenureExpired() {
        // Current tenure (future end date)
        assertFalse(member.hasTenureExpired());

        // Expired tenure
        member.setTenureEndDate(LocalDate.now().minusDays(1));
        assertTrue(member.hasTenureExpired());

        // No end date (indefinite tenure)
        member.setTenureEndDate(null);
        assertFalse(member.hasTenureExpired());
    }

    @Test
    void testIsCurrentlyServingWithNullDates() {
        // No start date
        member.setTenureStartDate(null);
        assertTrue(member.isCurrentlyServing());

        // No end date
        member.setTenureEndDate(null);
        assertTrue(member.isCurrentlyServing());

        // Both null
        member.setTenureStartDate(null);
        member.setTenureEndDate(null);
        assertTrue(member.isCurrentlyServing());
    }

    @Test
    void testBiographyAndPhoto() {
        assertEquals("Experienced sports administrator", member.getBiography());
        
        member.setPhotoUrl("https://example.com/photo.jpg");
        assertEquals("https://example.com/photo.jpg", member.getPhotoUrl());
    }

    @Test
    void testDefaultValues() {
        Member newMember = new Member();
        assertTrue(newMember.getIsActive());
        assertFalse(newMember.getIsProminent());
        assertEquals(0, newMember.getHierarchyLevel());
    }

    @Test
    void testToString() {
        String toString = member.toString();
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("President"));
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("true"));
    }
}