package com.pengunie.health.health.event.tool;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengunie.health.health.event.entity.HealthEvent;
import com.pengunie.health.health.event.service.HealthEventRetrievalService;

@Component
public class HealthEventSearchTool {

    private final HealthEventRetrievalService retrievalService;

    public HealthEventSearchTool(
            HealthEventRetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    public List<HealthEvent> search(String name) {

        return retrievalService.findByName(name);
    }
}