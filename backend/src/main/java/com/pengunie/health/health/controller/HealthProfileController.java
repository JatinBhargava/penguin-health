package com.pengunie.health.health.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pengunie.health.health.dto.HealthProfileRequest;
import com.pengunie.health.health.dto.HealthProfileResponse;
import com.pengunie.health.health.service.HealthProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/health/profile")
public class HealthProfileController {

    private final HealthProfileService healthProfileService;

    public HealthProfileController(
            HealthProfileService healthProfileService) {
        this.healthProfileService = healthProfileService;
    }

    @GetMapping
    public ResponseEntity<HealthProfileResponse> getProfile() {
        return ResponseEntity.ok(
                healthProfileService.getProfile()
        );
    }

    @PutMapping
    public ResponseEntity<HealthProfileResponse> saveProfile(
            @Valid @RequestBody HealthProfileRequest request) {

        return ResponseEntity.ok(
                healthProfileService.saveProfile(request)
        );
    }
}