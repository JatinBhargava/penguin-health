package com.pengunie.health.health.event.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengunie.health.health.event.entity.HealthEvent;
import com.pengunie.health.health.event.repository.HealthEventRepository;

@Service
public class HealthEventRetrievalService {

    private static final String DEMO_USER_ID = "demo-user";

    private final HealthEventRepository healthEventRepository;

    public HealthEventRetrievalService(
            HealthEventRepository healthEventRepository) {
        this.healthEventRepository = healthEventRepository;
    }

    public List<HealthEvent> findByName(String name) {

        return healthEventRepository
                .findByUserIdAndNameContainingIgnoreCaseOrderByObservedAtDesc(
                        DEMO_USER_ID,
                        name
                );
    }

    public Optional<HealthEvent> findLatestByName(String name) {

        return healthEventRepository
                .findFirstByUserIdAndNameContainingIgnoreCaseOrderByObservedAtDesc(
                        DEMO_USER_ID,
                        name
                );
    }

    public List<HealthEvent> findHistoryByName(String name) {

        return healthEventRepository
                .findByUserIdAndNameContainingIgnoreCaseOrderByObservedAtDesc(
                        DEMO_USER_ID,
                        name
                );
    }
}
