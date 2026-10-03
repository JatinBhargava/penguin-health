package com.pengunie.health.embedding.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pengunie.health.embedding.service.DocumentEmbeddingService;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentEmbeddingController {

    private final DocumentEmbeddingService embeddingService;

    public DocumentEmbeddingController(
            DocumentEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/{documentId}/embed")
    public ResponseEntity<Map<String, String>> embed(
            @PathVariable UUID documentId) {

        embeddingService.embedDocument(documentId);

        return ResponseEntity.ok(
                Map.of(
                        "status", "embedded",
                        "documentId", documentId.toString()
                )
        );
    }
}