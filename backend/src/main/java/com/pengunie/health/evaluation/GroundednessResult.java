package com.pengunie.health.evaluation;

import java.util.List;

public record GroundednessResult(
        boolean grounded,
        double score,
        List<String> unsupportedClaims,
        String explanation
) {
}