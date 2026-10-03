package com.pengunie.health.rag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pengunie.health.rag.model.RagContext;
import com.pengunie.health.search.dto.SearchResult;
import com.pengunie.health.search.service.SemanticSearchService;

@Service
public class RagContextBuilder {

    private final SemanticSearchService searchService;

    public RagContextBuilder(
            SemanticSearchService searchService) {
        this.searchService = searchService;
    }

    public RagContext build(
            String query,
            int topK) {

        List<SearchResult> results =
                searchService.search(query, topK);

        return new RagContext(query, results);
    }
}