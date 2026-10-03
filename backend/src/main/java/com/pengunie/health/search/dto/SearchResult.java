package com.pengunie.health.search.dto;

import java.util.UUID;

public record SearchResult(
        UUID chunkId,
        UUID documentId,
        int chunkIndex,
        String content,
        double distance
) {
}