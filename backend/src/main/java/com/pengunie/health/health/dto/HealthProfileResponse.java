package com.pengunie.health.health.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthProfileResponse {

    private UUID id;
    private String userId;
    private LocalDate dateOfBirth;
    private String gender;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String bloodGroup;
    private String allergies;
    private String existingConditions;
    private String medications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}