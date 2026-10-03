package com.pengunie.health.health.event.tool;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.pengunie.health.health.event.entity.HealthEvent;
import com.pengunie.health.health.event.service.HealthEventRetrievalService;

@Component
public class LatestHealthEventTool {

    private final HealthEventRetrievalService retrievalService;

    public LatestHealthEventTool(
            HealthEventRetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    public Optional<HealthEvent> latest(String name) {

        return retrievalService.findLatestByName(name);
    }
}