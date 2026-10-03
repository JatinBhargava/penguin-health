package com.pengunie.health.health.event.tool;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pengunie.health.health.event.entity.HealthEvent;
import com.pengunie.health.health.event.service.HealthEventRetrievalService;

@Component
public class HealthHistoryTool {

    private final HealthEventRetrievalService retrievalService;

    public HealthHistoryTool(
            HealthEventRetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    public List<HealthEvent> history(String name) {

        return retrievalService.findHistoryByName(name);
    }
}