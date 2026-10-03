package com.pengunie.health.rag.model;

import java.util.List;

import com.pengunie.health.search.dto.SearchResult;

public record RagContext(
        String query,
        List<SearchResult> results
) {
}