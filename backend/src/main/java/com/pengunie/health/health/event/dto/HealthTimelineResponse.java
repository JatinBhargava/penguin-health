package com.pengunie.health.health.event.dto;

import java.time.LocalDate;
import java.util.UUID;

public record HealthTimelineResponse(
        UUID id,
        String eventType,
        String name,
        String value,
        String unit,
        String referenceRange,
        LocalDate observedAt,
        UUID documentId
) {}