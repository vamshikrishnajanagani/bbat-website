package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.member.ContactFormRequest;
import com.telangana.ballbadminton.dto.member.MemberRequest;
import com.telangana.ballbadminton.dto.member.MemberResponse;
import com.telangana.ballbadminton.service.FileUploadService;
import com.telangana.ballbadminton.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for member management operations
 * Provides CRUD endpoints for association members, hierarchy management, and contact forms
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "Member Management", description = "Association member management operations")
public class MemberController {

    private final MemberService memberService;
    private final FileUploadService fileUploadService;

    public MemberController(MemberService memberService, FileUploadService fileUploadService) {
        this.memberService = memberService;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Get all active members ordered by hierarchy
     */
    @GetMapping
    @Operation(summary = "Get all active members", description = "Retrieve all active members ordered by hierarchy level")
    @ApiResponse(responseCode = "200", description = "Members retrieved successfully")
    public ResponseEntity<List<MemberResponse>> getAllActiveMembers() {
        List<MemberResponse> members = memberService.getAllActiveMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * Get active members with pagination
     */
    @GetMapping("/paginated")
    @Operation(summary = "Get active members with pagination", description = "Retrieve active members with pagination and sorting")
    public ResponseEntity<Page<MemberResponse>> getActiveMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "hierarchyLevel") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Page<MemberResponse> memberPage = memberService.getActiveMembers(page, size, sortBy, sortDir);
        return ResponseEntity.ok(memberPage);
    }

    /**
     * Get member by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID", description = "Retrieve a specific member by ID")
    @ApiResponse(responseCode = "200", description = "Member retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Member not found")
    public ResponseEntity<MemberResponse> getMemberById(
            @Parameter(description = "Member ID") @PathVariable UUID id) {
        
        return memberService.getMemberById(id)
                .map(member -> ResponseEntity.ok(member))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get prominent members
     */
    @GetMapping("/prominent")
    @Operation(summary = "Get prominent members", description = "Retrieve all prominent members")
    public ResponseEntity<List<MemberResponse>> getProminentMembers() {
        List<MemberResponse> members = memberService.getProminentMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * Get currently serving members
     */
    @GetMapping("/currently-serving")
    @Operation(summary = "Get currently serving members", description = "Retrieve members who are currently serving")
    public ResponseEntity<List<MemberResponse>> getCurrentlyServingMembers() {
        List<MemberResponse> members = memberService.getCurrentlyServingMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * Get top-level members (hierarchy level 1-3)
     */
    @GetMapping("/top-level")
    @Operation(summary = "Get top-level members", description = "Retrieve top-level members (hierarchy level 1-3)")
    public ResponseEntity<List<MemberResponse>> getTopLevelMembers() {
        List<MemberResponse> members = memberService.getTopLevelMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * Search members
     */
    @GetMapping("/search")
    @Operation(summary = "Search members", description = "Search members by name, position, or email")
    public ResponseEntity<Page<MemberResponse>> searchMembers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<MemberResponse> memberPage = memberService.searchMembers(q, page, size);
        return ResponseEntity.ok(memberPage);
    }

    /**
     * Create new member - requires MEMBER_CREATE permission
     */
    @PostMapping
    @Operation(summary = "Create new member", description = "Create a new association member")
    @ApiResponse(responseCode = "201", description = "Member created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid member data")
    @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('PERMISSION_MEMBER_CREATE')")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse member = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    /**
     * Update existing member - requires MEMBER_UPDATE permission
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update member", description = "Update an existing member")
    @ApiResponse(responseCode = "200", description = "Member updated successfully")
    @ApiResponse(responseCode = "404", description = "Member not found")
    @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('PERMISSION_MEMBER_UPDATE')")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody MemberRequest request) {
        
        MemberResponse member = memberService.updateMember(id, request);
        return ResponseEntity.ok(member);
    }

    /**
     * Delete member - requires MEMBER_DELETE permission
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete member", description = "Delete a member (soft delete)")
    @ApiResponse(responseCode = "204", description = "Member deleted successfully")
    @ApiResponse(responseCode = "404", description = "Member not found")
    @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('PERMISSION_MEMBER_DELETE')")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload member photo - requires MEMBER_UPDATE permission
     */
    @PostMapping("/{id}/photo")
    @Operation(summary = "Upload member photo", description = "Upload a photo for a member")
    @ApiResponse(responseCode = "200", description = "Photo uploaded successfully")
    @ApiResponse(responseCode = "404", description = "Member not found")
    @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('PERMISSION_MEMBER_UPDATE')")
    public ResponseEntity<MemberResponse> uploadMemberPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // Upload file and get URL
            String photoUrl = fileUploadService.uploadMemberPhoto(file);
            
            // Update member with new photo URL
            MemberResponse member = memberService.updateMemberPhoto(id, photoUrl);
            
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Submit contact form
     */
    @PostMapping("/contact")
    @Operation(summary = "Submit contact form", description = "Submit a contact form to reach a member or general contact")
    @ApiResponse(responseCode = "200", description = "Contact form submitted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid contact form data")
    public ResponseEntity<Map<String, String>> submitContactForm(@Valid @RequestBody ContactFormRequest request) {
        try {
            memberService.handleContactForm(request);
            return ResponseEntity.ok(Map.of("message", "Contact form submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to submit contact form: " + e.getMessage()));
        }
    }

    /**
     * Get member statistics - requires MEMBER_READ permission
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get member statistics", description = "Get member statistics and counts")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('PERMISSION_MEMBER_READ')")
    public ResponseEntity<MemberService.MemberStatistics> getMemberStatistics() {
        MemberService.MemberStatistics stats = memberService.getMemberStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get members with tenure ending soon - requires MEMBER_READ permission
     */
    @GetMapping("/tenure-ending-soon")
    @Operation(summary = "Get members with tenure ending soon", description = "Get members whose tenure is ending within 30 days")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('PERMISSION_MEMBER_READ')")
    public ResponseEntity<List<MemberResponse>> getMembersWithTenureEndingSoon() {
        List<MemberResponse> members = memberService.getMembersWithTenureEndingSoon();
        return ResponseEntity.ok(members);
    }
}