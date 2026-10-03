package com.pengunie.health.health.event.dto;

import java.time.LocalDate;

public record ExtractedHealthEvent(
        String eventType,
        String name,
        String value,
        String unit,
        String referenceRange,
        LocalDate observedAt,
        Integer sourceChunkIndex
) {}