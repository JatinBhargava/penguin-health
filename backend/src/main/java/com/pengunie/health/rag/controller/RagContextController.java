package com.pengunie.health.rag.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pengunie.health.rag.model.RagContext;
import com.pengunie.health.rag.service.RagContextBuilder;

@RestController
@RequestMapping("/api/v1/rag")
public class RagContextController {

    private final RagContextBuilder contextBuilder;

    public RagContextController(
            RagContextBuilder contextBuilder) {
        this.contextBuilder = contextBuilder;
    }

    @GetMapping("/context")
    public RagContext context(
            @RequestParam String query,
            @RequestParam(defaultValue = "3") int topK) {

        return contextBuilder.build(query, topK);
    }
}