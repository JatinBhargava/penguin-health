package com.pengunie.health.health.event.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pengunie.health.document.entity.DocumentChunk;
import com.pengunie.health.document.repository.DocumentChunkRepository;
import com.pengunie.health.health.event.dto.ExtractedHealthEvent;
import com.pengunie.health.health.event.dto.HealthTimelineResponse;
import com.pengunie.health.health.event.entity.HealthEvent;
import com.pengunie.health.health.event.repository.HealthEventRepository;

@Service
public class HealthEventService {

    private static final String DEMO_USER_ID = "demo-user";

    private final HealthEventRepository healthEventRepository;
    private final HealthEventExtractionService extractionService;
    private final DocumentChunkRepository documentChunkRepository;

    public HealthEventService(
            HealthEventRepository healthEventRepository,
            HealthEventExtractionService extractionService,
            DocumentChunkRepository documentChunkRepository) {

        this.healthEventRepository = healthEventRepository;
        this.extractionService = extractionService;
        this.documentChunkRepository = documentChunkRepository;
    }

    @Transactional
    public List<HealthEvent> extractAndSave(
            UUID documentId,
            String documentText) {

        List<DocumentChunk> chunks =
                documentChunkRepository
                        .findByDocumentIdOrderByChunkIndex(documentId);

        if (chunks.isEmpty()) {
            throw new IllegalStateException(
                    "No chunks found for document: " + documentId
            );
        }

        String chunkedText = chunks.stream()
                .map(chunk -> """
                        [Chunk %d]
                        %s
                        """.formatted(
                        chunk.getChunkIndex(),
                        chunk.getContent()
                ))
                .collect(Collectors.joining("\n\n"));

        List<ExtractedHealthEvent> extractedEvents =
                extractionService.extract(chunkedText);

        Map<Integer, UUID> chunkIds = chunks.stream()
                .collect(Collectors.toMap(
                        DocumentChunk::getChunkIndex,
                        DocumentChunk::getId
                ));

        List<HealthEvent> events = extractedEvents.stream()
                .map(event -> toEntity(
                        documentId,
                        event,
                        chunkIds
                ))
                .toList();

        return healthEventRepository.saveAll(events);
    }

    private HealthEvent toEntity(
            UUID documentId,
            ExtractedHealthEvent event,
            Map<Integer, UUID> chunkIds) {

        HealthEvent healthEvent = new HealthEvent();

        healthEvent.setUserId(DEMO_USER_ID);
        healthEvent.setDocumentId(documentId);

        healthEvent.setEventType(event.eventType());
        healthEvent.setName(event.name());
        healthEvent.setValue(event.value());
        healthEvent.setUnit(event.unit());
        healthEvent.setReferenceRange(event.referenceRange());
        healthEvent.setObservedAt(event.observedAt());

        Integer chunkIndex = event.sourceChunkIndex();

        if (chunkIndex != null) {

            UUID sourceChunkId = chunkIds.get(chunkIndex);

            if (sourceChunkId == null) {
                throw new IllegalStateException(
                        "Invalid source chunk index: " + chunkIndex
                );
            }

            healthEvent.setSourceChunkId(sourceChunkId);
        }

        return healthEvent;
    }

    public List<HealthTimelineResponse> getTimeline() {

        return healthEventRepository
                .findByUserIdOrderByObservedAtDesc(DEMO_USER_ID)
                .stream()
                .map(event -> new HealthTimelineResponse(
                        event.getId(),
                        event.getEventType(),
                        event.getName(),
                        event.getValue(),
                        event.getUnit(),
                        event.getReferenceRange(),
                        event.getObservedAt(),
                        event.getDocumentId()
                ))
                .toList();
    }
}