package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.district.DistrictRequest;
import com.telangana.ballbadminton.dto.district.DistrictResponse;
import com.telangana.ballbadminton.dto.player.PlayerResponse;
import com.telangana.ballbadminton.dto.tournament.TournamentResponse;
import com.telangana.ballbadminton.entity.District;
import com.telangana.ballbadminton.entity.Player;
import com.telangana.ballbadminton.entity.Tournament;
import com.telangana.ballbadminton.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for District management
 */
@Service
@Transactional
public class DistrictService {

    private final DistrictRepository districtRepository;

    @Autowired
    public DistrictService(DistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }

    /**
     * Get all active districts
     */
    @Cacheable(value = "districts", key = "'all-active'")
    @Transactional(readOnly = true)
    public List<DistrictResponse> getAllActiveDistricts() {
        return districtRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get districts with pagination
     */
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getDistricts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return districtRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get district by ID
     */
    @Cacheable(value = "districts", key = "#id")
    @Transactional(readOnly = true)
    public Optional<DistrictResponse> getDistrictById(UUID id) {
        return districtRepository.findById(id)
                .map(this::convertToResponse);
    }

    /**
     * Get district by code
     */
    @Transactional(readOnly = true)
    public Optional<DistrictResponse> getDistrictByCode(String code) {
        return districtRepository.findByCodeAndIsActiveTrue(code)
                .map(this::convertToResponse);
    }

    /**
     * Search districts by name
     */
    @Transactional(readOnly = true)
    public Page<DistrictResponse> searchDistricts(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return districtRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get districts with statistics
     */
    @Transactional(readOnly = true)
    public List<DistrictResponse> getDistrictsWithStatistics() {
        return districtRepository.findAllWithStatistics()
                .stream()
                .map(this::convertToResponseWithStats)
                .collect(Collectors.toList());
    }

    /**
     * Create new district
     */
    @CacheEvict(value = "districts", allEntries = true)
    public DistrictResponse createDistrict(DistrictRequest request) {
        if (districtRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new IllegalArgumentException("District with code " + request.getCode() + " already exists");
        }

        District district = convertToEntity(request);
        District savedDistrict = districtRepository.save(district);
        return convertToResponse(savedDistrict);
    }

    /**
     * Update district
     */
    @Caching(evict = {
        @CacheEvict(value = "districts", key = "#id"),
        @CacheEvict(value = "districts", key = "'all-active'")
    })
    public DistrictResponse updateDistrict(UUID id, DistrictRequest request) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("District not found with id: " + id));

        // Check if code is being changed and if new code already exists
        if (!district.getCode().equals(request.getCode()) && 
            districtRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new IllegalArgumentException("District with code " + request.getCode() + " already exists");
        }

        updateEntityFromRequest(district, request);
        District savedDistrict = districtRepository.save(district);
        return convertToResponse(savedDistrict);
    }

    /**
     * Delete district (soft delete)
     */
    @Caching(evict = {
        @CacheEvict(value = "districts", key = "#id"),
        @CacheEvict(value = "districts", key = "'all-active'")
    })
    public void deleteDistrict(UUID id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("District not found with id: " + id));
        
        district.setIsActive(false);
        districtRepository.save(district);
    }

    /**
     * Get district statistics
     */
    @Transactional(readOnly = true)
    public DistrictStatistics getDistrictStatistics() {
        long totalDistricts = districtRepository.countByIsActiveTrue();
        List<District> districtsWithStats = districtRepository.findAllWithStatistics();
        
        long totalPlayers = districtsWithStats.stream()
                .mapToLong(d -> d.getPlayers().size())
                .sum();
        
        long totalTournaments = districtsWithStats.stream()
                .mapToLong(d -> d.getTournaments().size())
                .sum();

        return new DistrictStatistics(totalDistricts, totalPlayers, totalTournaments);
    }

    /**
     * Get players by district with pagination
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPlayersByDistrict(UUID districtId, int page, int size) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new IllegalArgumentException("District not found with id: " + districtId));

        List<Player> allPlayers = district.getPlayers();
        int start = page * size;
        int end = Math.min(start + size, allPlayers.size());
        
        List<PlayerResponse> players = allPlayers.subList(start, end).stream()
                .map(this::convertPlayerToResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("players", players);
        result.put("totalElements", allPlayers.size());
        result.put("totalPages", (int) Math.ceil((double) allPlayers.size() / size));
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        return result;
    }

    /**
     * Get tournaments by district with pagination
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTournamentsByDistrict(UUID districtId, int page, int size) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new IllegalArgumentException("District not found with id: " + districtId));

        List<Tournament> allTournaments = district.getTournaments();
        int start = page * size;
        int end = Math.min(start + size, allTournaments.size());
        
        List<TournamentResponse> tournaments = allTournaments.subList(start, end).stream()
                .map(this::convertTournamentToResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("tournaments", tournaments);
        result.put("totalElements", allTournaments.size());
        result.put("totalPages", (int) Math.ceil((double) allTournaments.size() / size));
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        return result;
    }

    /**
     * Get district analytics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDistrictAnalytics(UUID districtId) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new IllegalArgumentException("District not found with id: " + districtId));

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("districtName", district.getName());
        analytics.put("totalPlayers", district.getPlayers().size());
        analytics.put("totalTournaments", district.getTournaments().size());
        
        // Player category breakdown
        Map<String, Long> playersByCategory = district.getPlayers().stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(p -> p.getCategory().getDisplayName(), Collectors.counting()));
        analytics.put("playersByCategory", playersByCategory);
        
        // Tournament status breakdown
        Map<String, Long> tournamentsByStatus = district.getTournaments().stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(t -> t.getStatus().getDisplayName(), Collectors.counting()));
        analytics.put("tournamentsByStatus", tournamentsByStatus);
        
        // Active players (those with recent activity)
        long activePlayers = district.getPlayers().stream()
                .filter(Player::getIsActive)
                .count();
        analytics.put("activePlayers", activePlayers);
        
        return analytics;
    }

    /**
     * Find districts near a location
     */
    @Transactional(readOnly = true)
    public List<DistrictResponse> findNearbyDistricts(Double latitude, Double longitude, Double radiusKm) {
        List<District> allDistricts = districtRepository.findByIsActiveTrueOrderByName();
        
        return allDistricts.stream()
                .filter(d -> d.getLatitude() != null && d.getLongitude() != null)
                .filter(d -> {
                    double distance = calculateDistance(
                            latitude, longitude,
                            d.getLatitude().doubleValue(), d.getLongitude().doubleValue()
                    );
                    return distance <= radiusKm;
                })
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }

    // Helper methods
    private DistrictResponse convertToResponse(District district) {
        DistrictResponse response = new DistrictResponse();
        response.setId(district.getId());
        response.setName(district.getName());
        response.setCode(district.getCode());
        response.setHeadquarters(district.getHeadquarters());
        response.setAreaSqKm(district.getAreaSqKm());
        response.setPopulation(district.getPopulation());
        response.setLatitude(district.getLatitude());
        response.setLongitude(district.getLongitude());
        response.setContactPerson(district.getContactPerson());
        response.setContactEmail(district.getContactEmail());
        response.setContactPhone(district.getContactPhone());
        response.setDescription(district.getDescription());
        response.setIsActive(district.getIsActive());
        response.setCreatedAt(district.getCreatedAt());
        response.setUpdatedAt(district.getUpdatedAt());
        return response;
    }

    private DistrictResponse convertToResponseWithStats(District district) {
        DistrictResponse response = convertToResponse(district);
        response.setPlayerCount((long) district.getPlayers().size());
        response.setTournamentCount((long) district.getTournaments().size());
        return response;
    }

    private District convertToEntity(DistrictRequest request) {
        District district = new District();
        updateEntityFromRequest(district, request);
        return district;
    }

    private void updateEntityFromRequest(District district, DistrictRequest request) {
        district.setName(request.getName());
        district.setCode(request.getCode());
        district.setHeadquarters(request.getHeadquarters());
        district.setAreaSqKm(request.getAreaSqKm());
        district.setPopulation(request.getPopulation());
        district.setLatitude(request.getLatitude());
        district.setLongitude(request.getLongitude());
        district.setContactPerson(request.getContactPerson());
        district.setContactEmail(request.getContactEmail());
        district.setContactPhone(request.getContactPhone());
        district.setDescription(request.getDescription());
        district.setIsActive(request.getIsActive());
    }

    private PlayerResponse convertPlayerToResponse(Player player) {
        PlayerResponse response = new PlayerResponse();
        response.setId(player.getId());
        response.setName(player.getName());
        response.setDateOfBirth(player.getDateOfBirth());
        response.setCategory(player.getCategory());
        response.setProfilePhotoUrl(player.getProfilePhotoUrl());
        response.setIsProminent(player.getIsProminent());
        response.setIsActive(player.getIsActive());
        if (player.getDistrict() != null) {
            response.setDistrictId(player.getDistrict().getId());
            response.setDistrictName(player.getDistrict().getName());
        }
        return response;
    }

    private TournamentResponse convertTournamentToResponse(Tournament tournament) {
        TournamentResponse response = new TournamentResponse();
        response.setId(tournament.getId());
        response.setName(tournament.getName());
        response.setDescription(tournament.getDescription());
        response.setStartDate(tournament.getStartDate());
        response.setEndDate(tournament.getEndDate());
        response.setVenue(tournament.getVenue());
        response.setStatus(tournament.getStatus());
        response.setRegistrationEndDate(tournament.getRegistrationEndDate());
        response.setMaxParticipants(tournament.getMaxParticipants());
        response.setEntryFee(tournament.getEntryFee());
        if (tournament.getDistrict() != null) {
            response.setDistrictId(tournament.getDistrict().getId());
            response.setDistrictName(tournament.getDistrict().getName());
        }
        return response;
    }

    // Statistics class
    public static class DistrictStatistics {
        private final long totalDistricts;
        private final long totalPlayers;
        private final long totalTournaments;

        public DistrictStatistics(long totalDistricts, long totalPlayers, long totalTournaments) {
            this.totalDistricts = totalDistricts;
            this.totalPlayers = totalPlayers;
            this.totalTournaments = totalTournaments;
        }

        public long getTotalDistricts() { return totalDistricts; }
        public long getTotalPlayers() { return totalPlayers; }
        public long getTotalTournaments() { return totalTournaments; }
    }
}