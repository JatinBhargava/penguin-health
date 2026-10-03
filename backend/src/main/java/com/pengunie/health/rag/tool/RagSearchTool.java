package com.pengunie.health.rag.tool;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengunie.health.search.dto.SearchResult;
import com.pengunie.health.search.service.SemanticSearchService;

@Component
public class RagSearchTool {

    private final SemanticSearchService semanticSearchService;

    public RagSearchTool(
            SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    public List<SearchResult> search(String query, int topK) {

        return semanticSearchService.search(query, topK);
    }
}