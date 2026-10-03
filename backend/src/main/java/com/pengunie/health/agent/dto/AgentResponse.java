package com.pengunie.health.agent.dto;

public record AgentResponse(
        String answer,
        String toolUsed
) {}