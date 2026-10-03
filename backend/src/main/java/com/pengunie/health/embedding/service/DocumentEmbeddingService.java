package com.pengunie.health.embedding.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pengunie.health.document.entity.DocumentChunk;
import com.pengunie.health.document.repository.DocumentChunkRepository;
import com.pengunie.health.embedding.repository.EmbeddingRepository;

@Service
public class DocumentEmbeddingService {

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;
    private final EmbeddingRepository embeddingRepository;

    public DocumentEmbeddingService(
            DocumentChunkRepository chunkRepository,
            EmbeddingService embeddingService,
            EmbeddingRepository embeddingRepository) {

        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
        this.embeddingRepository = embeddingRepository;
    }

    @Transactional
    public void embedDocument(UUID documentId) {

        List<DocumentChunk> chunks
                = chunkRepository
                        .findByDocumentIdOrderByChunkIndex(documentId);

        for (DocumentChunk chunk : chunks) {

            List<Float> embedding
                    = embeddingService.createEmbedding(
                            chunk.getContent()
                    );

            embeddingRepository.saveEmbedding(
                    chunk.getId(),
                    embedding
            );
        }
    }
}
