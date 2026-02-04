package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.district.DistrictRequest;
import com.telangana.ballbadminton.dto.district.DistrictResponse;
import com.telangana.ballbadminton.service.DistrictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for District management
 */
@RestController
@RequestMapping("/api/v1/districts")
@Tag(name = "Districts", description = "District management operations")
public class DistrictController {

    private final DistrictService districtService;

    @Autowired
    public DistrictController(DistrictService districtService) {
        this.districtService = districtService;
    }

    @GetMapping
    @Operation(summary = "Get all active districts")
    public ResponseEntity<List<DistrictResponse>> getAllActiveDistricts() {
        List<DistrictResponse> districts = districtService.getAllActiveDistricts();
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get districts with pagination")
    public ResponseEntity<Page<DistrictResponse>> getDistricts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<DistrictResponse> districts = districtService.getDistricts(page, size, sortBy, sortDir);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get district by ID")
    public ResponseEntity<DistrictResponse> getDistrictById(@PathVariable UUID id) {
        return districtService.getDistrictById(id)
                .map(district -> ResponseEntity.ok(district))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get district by code")
    public ResponseEntity<DistrictResponse> getDistrictByCode(@PathVariable String code) {
        return districtService.getDistrictByCode(code)
                .map(district -> ResponseEntity.ok(district))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search districts by name")
    public ResponseEntity<Page<DistrictResponse>> searchDistricts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DistrictResponse> districts = districtService.searchDistricts(name, page, size);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get districts with statistics")
    public ResponseEntity<List<DistrictResponse>> getDistrictsWithStatistics() {
        List<DistrictResponse> districts = districtService.getDistrictsWithStatistics();
        return ResponseEntity.ok(districts);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_DISTRICT_CREATE')")
    @Operation(summary = "Create new district")
    public ResponseEntity<DistrictResponse> createDistrict(@Valid @RequestBody DistrictRequest request) {
        DistrictResponse district = districtService.createDistrict(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(district);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DISTRICT_UPDATE')")
    @Operation(summary = "Update district")
    public ResponseEntity<DistrictResponse> updateDistrict(
            @PathVariable UUID id,
            @Valid @RequestBody DistrictRequest request) {
        DistrictResponse district = districtService.updateDistrict(id, request);
        return ResponseEntity.ok(district);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DISTRICT_DELETE')")
    @Operation(summary = "Delete district")
    public ResponseEntity<Void> deleteDistrict(@PathVariable UUID id) {
        districtService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('PERMISSION_DISTRICT_READ')")
    @Operation(summary = "Get district statistics")
    public ResponseEntity<Map<String, Object>> getDistrictStatistics() {
        DistrictService.DistrictStatistics stats = districtService.getDistrictStatistics();
        Map<String, Object> response = Map.of(
                "totalDistricts", stats.getTotalDistricts(),
                "totalPlayers", stats.getTotalPlayers(),
                "totalTournaments", stats.getTotalTournaments()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/players")
    @Operation(summary = "Get players by district")
    public ResponseEntity<Map<String, Object>> getPlayersByDistrict(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = districtService.getPlayersByDistrict(id, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/tournaments")
    @Operation(summary = "Get tournaments by district")
    public ResponseEntity<Map<String, Object>> getTournamentsByDistrict(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = districtService.getTournamentsByDistrict(id, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/analytics")
    @Operation(summary = "Get district analytics")
    public ResponseEntity<Map<String, Object>> getDistrictAnalytics(@PathVariable UUID id) {
        Map<String, Object> analytics = districtService.getDistrictAnalytics(id);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find districts near location")
    public ResponseEntity<List<DistrictResponse>> findNearbyDistricts(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "100") Double radiusKm) {
        List<DistrictResponse> districts = districtService.findNearbyDistricts(latitude, longitude, radiusKm);
        return ResponseEntity.ok(districts);
    }
}