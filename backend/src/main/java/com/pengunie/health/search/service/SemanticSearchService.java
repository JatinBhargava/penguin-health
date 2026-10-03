package com.pengunie.health.search.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pengunie.health.embedding.service.EmbeddingService;
import com.pengunie.health.search.dto.SearchResult;
import com.pengunie.health.search.repository.VectorSearchRepository;

@Service
public class SemanticSearchService {

    private final EmbeddingService embeddingService;
    private final VectorSearchRepository searchRepository;

    public SemanticSearchService(
            EmbeddingService embeddingService,
            VectorSearchRepository searchRepository) {

        this.embeddingService = embeddingService;
        this.searchRepository = searchRepository;
    }

    public List<SearchResult> search(
            String query,
            int limit) {

        List<Float> queryEmbedding =
                embeddingService.createEmbedding(query);

        return searchRepository.search(
                queryEmbedding,
                limit
        );
    }
}