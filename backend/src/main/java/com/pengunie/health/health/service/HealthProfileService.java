package com.pengunie.health.health.service;

import com.pengunie.health.health.dto.HealthProfileRequest;
import com.pengunie.health.health.dto.HealthProfileResponse;
import com.pengunie.health.health.entity.HealthProfile;
import com.pengunie.health.health.repository.HealthProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthProfileService {

    private static final String DEMO_USER_ID = "demo-user";

    private final HealthProfileRepository healthProfileRepository;

    public HealthProfileService(HealthProfileRepository healthProfileRepository) {
        this.healthProfileRepository = healthProfileRepository;
    }

    @Transactional(readOnly = true)
    public HealthProfileResponse getProfile() {
        HealthProfile profile = healthProfileRepository
                .findByUserId(DEMO_USER_ID)
                .orElseThrow(() ->
                        new RuntimeException("Health profile not found"));

        return toResponse(profile);
    }

    @Transactional
    public HealthProfileResponse saveProfile(HealthProfileRequest request) {

        HealthProfile profile = healthProfileRepository
                .findByUserId(DEMO_USER_ID)
                .orElseGet(HealthProfile::new);

        profile.setUserId(DEMO_USER_ID);
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setBloodGroup(request.getBloodGroup());
        profile.setAllergies(request.getAllergies());
        profile.setExistingConditions(request.getExistingConditions());
        profile.setMedications(request.getMedications());

        HealthProfile saved = healthProfileRepository.save(profile);

        return toResponse(saved);
    }

    private HealthProfileResponse toResponse(HealthProfile profile) {
        HealthProfileResponse response = new HealthProfileResponse();

        response.setId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setGender(profile.getGender());
        response.setHeightCm(profile.getHeightCm());
        response.setWeightKg(profile.getWeightKg());
        response.setBloodGroup(profile.getBloodGroup());
        response.setAllergies(profile.getAllergies());
        response.setExistingConditions(profile.getExistingConditions());
        response.setMedications(profile.getMedications());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        return response;
    }
}