package com.pengunie.health.health.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pengunie.health.health.entity.HealthProfile;

public interface HealthProfileRepository
        extends JpaRepository<HealthProfile, UUID> {

    Optional<HealthProfile> findByUserId(String userId);
}