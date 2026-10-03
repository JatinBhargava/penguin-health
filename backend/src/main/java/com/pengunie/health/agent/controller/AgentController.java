package com.pengunie.health.agent.controller;

import com.pengunie.health.agent.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public ResponseEntity<String> ask(
            @RequestParam String question) {

        return ResponseEntity.ok(
                agentService.ask(question)
        );
    }
}