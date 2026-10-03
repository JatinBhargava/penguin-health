package com.pengunie.health.health.event.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pengunie.health.health.event.entity.HealthEvent;

public interface HealthEventRepository
        extends JpaRepository<HealthEvent, UUID> {

    List<HealthEvent> findByUserIdOrderByObservedAtDesc(
            String userId
    );

List<HealthEvent> findByUserIdAndNameContainingIgnoreCaseOrderByObservedAtDesc(
        String userId,
        String name
);

Optional<HealthEvent> findFirstByUserIdAndNameContainingIgnoreCaseOrderByObservedAtDesc(
        String userId,
        String name
);
}