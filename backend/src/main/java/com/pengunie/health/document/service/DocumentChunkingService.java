package com.pengunie.health.document.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pengunie.health.document.entity.DocumentChunk;
import com.pengunie.health.document.repository.DocumentChunkRepository;

@Service
public class DocumentChunkingService {

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    private final DocumentChunkRepository chunkRepository;
    

    public DocumentChunkingService(
            DocumentChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    public List<DocumentChunk> createChunks(
            UUID documentId,
            String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<DocumentChunk> chunks = new ArrayList<>();

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    text.length()
            );

            String content = text
                    .substring(start, end)
                    .trim();

            if (!content.isBlank()) {

                DocumentChunk chunk = new DocumentChunk();

                chunk.setDocumentId(documentId);
                chunk.setChunkIndex(chunkIndex++);
                chunk.setContent(content);

                chunks.add(chunk);
            }

            if (end == text.length()) {
                break;
            }

            start = end - CHUNK_OVERLAP;
        }

        return chunkRepository.saveAll(chunks);
    }
}