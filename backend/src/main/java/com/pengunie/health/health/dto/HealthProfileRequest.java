package com.pengunie.health.health.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthProfileRequest {

    private LocalDate dateOfBirth;

    @Size(max = 30)
    private String gender;

    @DecimalMin("30.0")
    @DecimalMax("250.0")
    private BigDecimal heightCm;

    @DecimalMin("2.0")
    @DecimalMax("500.0")
    private BigDecimal weightKg;

    @Size(max = 10)
    private String bloodGroup;

    private String allergies;

    private String existingConditions;

    private String medications;
}