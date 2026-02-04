package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.member.ContactFormRequest;
import com.telangana.ballbadminton.dto.member.MemberRequest;
import com.telangana.ballbadminton.dto.member.MemberResponse;
import com.telangana.ballbadminton.entity.Member;
import com.telangana.ballbadminton.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MemberService
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private MemberRequest testMemberRequest;
    private UUID testMemberId;

    @BeforeEach
    void setUp() {
        testMemberId = UUID.randomUUID();
        
        testMember = new Member();
        testMember.setId(testMemberId);
        testMember.setName("John Doe");
        testMember.setPosition("President");
        testMember.setEmail("john.doe@example.com");
        testMember.setPhone("+91-9876543210");
        testMember.setBiography("Experienced sports administrator");
        testMember.setHierarchyLevel(1);
        testMember.setTenureStartDate(LocalDate.of(2023, 1, 1));
        testMember.setTenureEndDate(LocalDate.now().plusYears(1));
        testMember.setIsActive(true);
        testMember.setIsProminent(true);

        testMemberRequest = new MemberRequest();
        testMemberRequest.setName("John Doe");
        testMemberRequest.setPosition("President");
        testMemberRequest.setEmail("john.doe@example.com");
        testMemberRequest.setPhone("+91-9876543210");
        testMemberRequest.setBiography("Experienced sports administrator");
        testMemberRequest.setHierarchyLevel(1);
        testMemberRequest.setTenureStartDate(LocalDate.of(2023, 1, 1));
        testMemberRequest.setTenureEndDate(LocalDate.now().plusYears(1));
        testMemberRequest.setIsActive(true);
        testMemberRequest.setIsProminent(true);
    }

    @Test
    void getAllActiveMembers_ShouldReturnAllActiveMembers() {
        // Given
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findActiveOrderedByHierarchy()).thenReturn(members);

        // When
        List<MemberResponse> result = memberService.getAllActiveMembers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMember.getName(), result.get(0).getName());
        assertEquals(testMember.getPosition(), result.get(0).getPosition());
        verify(memberRepository).findActiveOrderedByHierarchy();
    }

    @Test
    void getActiveMembers_WithPagination_ShouldReturnPagedResults() {
        // Given
        List<Member> members = Arrays.asList(testMember);
        Page<Member> memberPage = new PageImpl<>(members);
        when(memberRepository.findActiveOrderedByHierarchy(any(Pageable.class))).thenReturn(memberPage);

        // When
        Page<MemberResponse> result = memberService.getActiveMembers(0, 10, "name", "asc");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testMember.getName(), result.getContent().get(0).getName());
        verify(memberRepository).findActiveOrderedByHierarchy(any(Pageable.class));
    }

    @Test
    void getMemberById_WhenMemberExists_ShouldReturnMember() {
        // Given
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));

        // When
        Optional<MemberResponse> result = memberService.getMemberById(testMemberId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMember.getName(), result.get().getName());
        assertEquals(testMember.getPosition(), result.get().getPosition());
        verify(memberRepository).findById(testMemberId);
    }

    @Test
    void getMemberById_WhenMemberNotExists_ShouldReturnEmpty() {
        // Given
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.empty());

        // When
        Optional<MemberResponse> result = memberService.getMemberById(testMemberId);

        // Then
        assertFalse(result.isPresent());
        verify(memberRepository).findById(testMemberId);
    }

    @Test
    void getProminentMembers_ShouldReturnProminentMembers() {
        // Given
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findByIsActiveTrueAndIsProminentTrue()).thenReturn(members);

        // When
        List<MemberResponse> result = memberService.getProminentMembers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsProminent());
        verify(memberRepository).findByIsActiveTrueAndIsProminentTrue();
    }

    @Test
    void getCurrentlyServingMembers_ShouldReturnCurrentlyServingMembers() {
        // Given
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findCurrentlyServing(any(LocalDate.class))).thenReturn(members);

        // When
        List<MemberResponse> result = memberService.getCurrentlyServingMembers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberRepository).findCurrentlyServing(any(LocalDate.class));
    }

    @Test
    void searchMembers_ShouldReturnMatchingMembers() {
        // Given
        List<Member> members = Arrays.asList(testMember);
        Page<Member> memberPage = new PageImpl<>(members);
        when(memberRepository.searchMembers(eq("John"), any(Pageable.class))).thenReturn(memberPage);

        // When
        Page<MemberResponse> result = memberService.searchMembers("John", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testMember.getName(), result.getContent().get(0).getName());
        verify(memberRepository).searchMembers(eq("John"), any(Pageable.class));
    }

    @Test
    void createMember_WithValidData_ShouldCreateMember() {
        // Given
        when(memberRepository.existsByEmail(testMemberRequest.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When
        MemberResponse result = memberService.createMember(testMemberRequest);

        // Then
        assertNotNull(result);
        assertEquals(testMemberRequest.getName(), result.getName());
        assertEquals(testMemberRequest.getPosition(), result.getPosition());
        verify(memberRepository).existsByEmail(testMemberRequest.getEmail());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void createMember_WithExistingEmail_ShouldThrowException() {
        // Given
        when(memberRepository.existsByEmail(testMemberRequest.getEmail())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> memberService.createMember(testMemberRequest));
        
        assertEquals("Email already exists: " + testMemberRequest.getEmail(), exception.getMessage());
        verify(memberRepository).existsByEmail(testMemberRequest.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void updateMember_WithValidData_ShouldUpdateMember() {
        // Given
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When
        MemberResponse result = memberService.updateMember(testMemberId, testMemberRequest);

        // Then
        assertNotNull(result);
        assertEquals(testMemberRequest.getName(), result.getName());
        verify(memberRepository).findById(testMemberId);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void updateMember_WhenMemberNotExists_ShouldThrowException() {
        // Given
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> memberService.updateMember(testMemberId, testMemberRequest));
        
        assertEquals("Member not found with ID: " + testMemberId, exception.getMessage());
        verify(memberRepository).findById(testMemberId);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void deleteMember_WhenMemberExists_ShouldDeactivateMember() {
        // Given
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When
        memberService.deleteMember(testMemberId);

        // Then
        verify(memberRepository).findById(testMemberId);
        verify(memberRepository).save(argThat(member -> !member.getIsActive()));
    }

    @Test
    void deleteMember_WhenMemberNotExists_ShouldThrowException() {
        // Given
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> memberService.deleteMember(testMemberId));
        
        assertEquals("Member not found with ID: " + testMemberId, exception.getMessage());
        verify(memberRepository).findById(testMemberId);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void updateMemberPhoto_WhenMemberExists_ShouldUpdatePhoto() {
        // Given
        String newPhotoUrl = "https://example.com/new-photo.jpg";
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When
        MemberResponse result = memberService.updateMemberPhoto(testMemberId, newPhotoUrl);

        // Then
        assertNotNull(result);
        verify(memberRepository).findById(testMemberId);
        verify(memberRepository).save(argThat(member -> newPhotoUrl.equals(member.getPhotoUrl())));
    }

    @Test
    void handleContactForm_WithMemberId_ShouldSendEmailToMember() {
        // Given
        ContactFormRequest request = new ContactFormRequest();
        request.setMemberId(testMemberId);
        request.setSenderName("Jane Smith");
        request.setSenderEmail("jane@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");

        when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));

        // When
        memberService.handleContactForm(request);

        // Then
        verify(memberRepository).findById(testMemberId);
        verify(emailService).sendContactFormEmail(
            eq(testMember.getEmail()),
            eq(testMember.getName()),
            eq(request.getSenderName()),
            eq(request.getSenderEmail()),
            eq(request.getSubject()),
            eq(request.getMessage())
        );
    }

    @Test
    void handleContactForm_WithoutMemberId_ShouldSendGeneralEmail() {
        // Given
        ContactFormRequest request = new ContactFormRequest();
        request.setSenderName("Jane Smith");
        request.setSenderEmail("jane@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");

        // When
        memberService.handleContactForm(request);

        // Then
        verify(emailService).sendGeneralContactFormEmail(
            eq(request.getSenderName()),
            eq(request.getSenderEmail()),
            eq(request.getSubject()),
            eq(request.getMessage())
        );
    }

    @Test
    void getMemberStatistics_ShouldReturnCorrectStatistics() {
        // Given
        when(memberRepository.countByIsActiveTrue()).thenReturn(10L);
        when(memberRepository.countByIsActiveTrueAndIsProminentTrue()).thenReturn(5L);
        when(memberRepository.countCurrentlyServing(any(LocalDate.class))).thenReturn(8L);

        // When
        MemberService.MemberStatistics result = memberService.getMemberStatistics();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalActive());
        assertEquals(5L, result.getProminent());
        assertEquals(8L, result.getCurrentlyServing());
        verify(memberRepository).countByIsActiveTrue();
        verify(memberRepository).countByIsActiveTrueAndIsProminentTrue();
        verify(memberRepository).countCurrentlyServing(any(LocalDate.class));
    }

    @Test
    void getMembersWithTenureEndingSoon_ShouldReturnMembersWithUpcomingTenureEnd() {
        // Given
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findWithTenureEndingSoon(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(members);

        // When
        List<MemberResponse> result = memberService.getMembersWithTenureEndingSoon();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberRepository).findWithTenureEndingSoon(any(LocalDate.class), any(LocalDate.class));
    }
}