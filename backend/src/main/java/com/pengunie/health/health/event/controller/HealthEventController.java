package com.pengunie.health.health.event.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pengunie.health.document.entity.Document;
import com.pengunie.health.document.repository.DocumentRepository;
import com.pengunie.health.health.event.dto.HealthTimelineResponse;
import com.pengunie.health.health.event.entity.HealthEvent;
import com.pengunie.health.health.event.service.HealthEventRetrievalService;
import com.pengunie.health.health.event.service.HealthEventService;

@RestController
@RequestMapping("/api/v1/health/events")
public class HealthEventController {

    private final DocumentRepository documentRepository;
    private final HealthEventService healthEventService;
    private final HealthEventRetrievalService healthEventRetrievalService;

    public HealthEventController(
            DocumentRepository documentRepository,
            HealthEventService healthEventService,
            HealthEventRetrievalService healthEventRetrievalService) {

        this.documentRepository = documentRepository;
        this.healthEventService = healthEventService;
        this.healthEventRetrievalService = healthEventRetrievalService;
    }

    @PostMapping("/extract/{documentId}")
    public ResponseEntity<List<HealthEvent>> extract(
            @PathVariable UUID documentId) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "Document not found: " + documentId));

        if (document.getExtractedText() == null
                || document.getExtractedText().isBlank()) {

            return ResponseEntity.badRequest().build();
        }

        List<HealthEvent> events
                = healthEventService.extractAndSave(
                        documentId,
                        document.getExtractedText());

        return ResponseEntity.ok(events);
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<HealthTimelineResponse>> getTimeline() {
        return ResponseEntity.ok(
                healthEventService.getTimeline()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<HealthEvent>> search(
            @RequestParam String name) {

        return ResponseEntity.ok(
                healthEventRetrievalService.findByName(name)
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<HealthEvent> latest(
            @RequestParam String name) {

        return healthEventRetrievalService
                .findLatestByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<HealthEvent>> history(
            @RequestParam String name) {

        return ResponseEntity.ok(
                healthEventRetrievalService.findHistoryByName(name)
        );
    }
}
