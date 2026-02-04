package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.member.ContactFormRequest;
import com.telangana.ballbadminton.dto.member.MemberRequest;
import com.telangana.ballbadminton.dto.member.MemberResponse;
import com.telangana.ballbadminton.entity.Member;
import com.telangana.ballbadminton.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for member management operations
 * Handles business logic for member CRUD operations, hierarchy management, and contact forms
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
@Transactional
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    public MemberService(MemberRepository memberRepository, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.emailService = emailService;
    }

    /**
     * Get all active members ordered by hierarchy
     */
    @Cacheable(value = "members", key = "'all-active'")
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllActiveMembers() {
        logger.debug("Fetching all active members");
        List<Member> members = memberRepository.findActiveOrderedByHierarchy();
        return members.stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get active members with pagination
     */
    @Transactional(readOnly = true)
    public Page<MemberResponse> getActiveMembers(int page, int size, String sortBy, String sortDir) {
        logger.debug("Fetching active members - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Member> memberPage = memberRepository.findActiveOrderedByHierarchy(pageable);
        return memberPage.map(MemberResponse::new);
    }

    /**
     * Get member by ID
     */
    @Cacheable(value = "members", key = "#id")
    @Transactional(readOnly = true)
    public Optional<MemberResponse> getMemberById(UUID id) {
        logger.debug("Fetching member by ID: {}", id);
        return memberRepository.findById(id)
                .map(MemberResponse::new);
    }

    /**
     * Get prominent members
     */
    @Cacheable(value = "members", key = "'prominent'")
    @Transactional(readOnly = true)
    public List<MemberResponse> getProminentMembers() {
        logger.debug("Fetching prominent members");
        List<Member> members = memberRepository.findByIsActiveTrueAndIsProminentTrue();
        return members.stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get currently serving members
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getCurrentlyServingMembers() {
        logger.debug("Fetching currently serving members");
        LocalDate currentDate = LocalDate.now();
        List<Member> members = memberRepository.findCurrentlyServing(currentDate);
        return members.stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get top-level members (hierarchy level 1-3)
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getTopLevelMembers() {
        logger.debug("Fetching top-level members");
        List<Member> members = memberRepository.findTopLevelMembers();
        return members.stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search members
     */
    @Transactional(readOnly = true)
    public Page<MemberResponse> searchMembers(String searchTerm, int page, int size) {
        logger.debug("Searching members with term: {}", searchTerm);
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> memberPage = memberRepository.searchMembers(searchTerm, pageable);
        return memberPage.map(MemberResponse::new);
    }

    /**
     * Create new member
     */
    @Caching(evict = {
        @CacheEvict(value = "members", key = "'all-active'"),
        @CacheEvict(value = "members", key = "'prominent'", condition = "#result.isProminent")
    })
    public MemberResponse createMember(MemberRequest request) {
        logger.info("Creating new member: {}", request.getName());
        
        // Validate email uniqueness
        if (request.getEmail() != null && memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        
        Member member = new Member();
        updateMemberFromRequest(member, request);
        
        Member savedMember = memberRepository.save(member);
        logger.info("Created member with ID: {}", savedMember.getId());
        
        return new MemberResponse(savedMember);
    }

    /**
     * Update existing member
     */
    @Caching(evict = {
        @CacheEvict(value = "members", key = "#id"),
        @CacheEvict(value = "members", key = "'all-active'"),
        @CacheEvict(value = "members", key = "'prominent'")
    })
    public MemberResponse updateMember(UUID id, MemberRequest request) {
        logger.info("Updating member with ID: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));
        
        // Validate email uniqueness (excluding current member)
        if (request.getEmail() != null && 
            !request.getEmail().equals(member.getEmail()) && 
            memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        
        updateMemberFromRequest(member, request);
        
        Member savedMember = memberRepository.save(member);
        logger.info("Updated member with ID: {}", savedMember.getId());
        
        return new MemberResponse(savedMember);
    }

    /**
     * Delete member (soft delete by setting inactive)
     */
    @Caching(evict = {
        @CacheEvict(value = "members", key = "#id"),
        @CacheEvict(value = "members", key = "'all-active'"),
        @CacheEvict(value = "members", key = "'prominent'")
    })
    public void deleteMember(UUID id) {
        logger.info("Deleting member with ID: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));
        
        member.setIsActive(false);
        memberRepository.save(member);
        
        logger.info("Deleted (deactivated) member with ID: {}", id);
    }

    /**
     * Update member photo
     */
    public MemberResponse updateMemberPhoto(UUID id, String photoUrl) {
        logger.info("Updating photo for member with ID: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));
        
        member.setPhotoUrl(photoUrl);
        Member savedMember = memberRepository.save(member);
        
        logger.info("Updated photo for member with ID: {}", id);
        return new MemberResponse(savedMember);
    }

    /**
     * Handle contact form submission
     */
    public void handleContactForm(ContactFormRequest request) {
        logger.info("Processing contact form for member ID: {}", request.getMemberId());
        
        Member member = null;
        if (request.getMemberId() != null) {
            member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + request.getMemberId()));
        }
        
        try {
            if (member != null && member.getEmail() != null) {
                // Send email to specific member
                emailService.sendContactFormEmail(
                    member.getEmail(),
                    member.getName(),
                    request.getSenderName(),
                    request.getSenderEmail(),
                    request.getSubject(),
                    request.getMessage()
                );
                logger.info("Contact form email sent to member: {}", member.getName());
            } else {
                // Send to general contact email
                emailService.sendGeneralContactFormEmail(
                    request.getSenderName(),
                    request.getSenderEmail(),
                    request.getSubject(),
                    request.getMessage()
                );
                logger.info("General contact form email sent");
            }
        } catch (Exception e) {
            logger.error("Failed to send contact form email", e);
            throw new RuntimeException("Failed to send contact form email", e);
        }
    }

    /**
     * Get member statistics
     */
    @Transactional(readOnly = true)
    public MemberStatistics getMemberStatistics() {
        logger.debug("Calculating member statistics");
        
        LocalDate currentDate = LocalDate.now();
        
        long totalActive = memberRepository.countByIsActiveTrue();
        long prominent = memberRepository.countByIsActiveTrueAndIsProminentTrue();
        long currentlyServing = memberRepository.countCurrentlyServing(currentDate);
        
        return new MemberStatistics(totalActive, prominent, currentlyServing);
    }

    /**
     * Get members with tenure ending soon (within next 30 days)
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersWithTenureEndingSoon() {
        logger.debug("Fetching members with tenure ending soon");
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        
        List<Member> members = memberRepository.findWithTenureEndingSoon(startDate, endDate);
        return members.stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to update member entity from request
     */
    private void updateMemberFromRequest(Member member, MemberRequest request) {
        member.setName(request.getName());
        member.setPosition(request.getPosition());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setBiography(request.getBiography());
        member.setPhotoUrl(request.getPhotoUrl());
        member.setHierarchyLevel(request.getHierarchyLevel());
        member.setTenureStartDate(request.getTenureStartDate());
        member.setTenureEndDate(request.getTenureEndDate());
        member.setIsActive(request.getIsActive());
        member.setIsProminent(request.getIsProminent());
    }

    /**
     * Inner class for member statistics
     */
    public static class MemberStatistics {
        private final long totalActive;
        private final long prominent;
        private final long currentlyServing;

        public MemberStatistics(long totalActive, long prominent, long currentlyServing) {
            this.totalActive = totalActive;
            this.prominent = prominent;
            this.currentlyServing = currentlyServing;
        }

        public long getTotalActive() { return totalActive; }
        public long getProminent() { return prominent; }
        public long getCurrentlyServing() { return currentlyServing; }
    }
}