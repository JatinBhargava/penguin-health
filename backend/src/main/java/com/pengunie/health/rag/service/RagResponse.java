package com.pengunie.health.rag.service;

import java.util.List;

import com.pengunie.health.search.dto.SearchResult;

public record RagResponse(
        String answer,
        List<SearchResult> results
) {
}