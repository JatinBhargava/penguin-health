package com.pengunie.health.rag.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pengunie.health.rag.service.RagAnswerService;

@RestController
@RequestMapping("/api/v1/rag")
public class RagController {

    private final RagAnswerService ragAnswerService;

    public RagController(RagAnswerService ragAnswerService) {
        this.ragAnswerService = ragAnswerService;
    }

    @GetMapping("/answer")
    public Map<String, Object> answer(
            @RequestParam String query,
            @RequestParam(defaultValue = "3") int topK) {

        String answer =
                ragAnswerService.answer(query, topK);

        return Map.of(
                "query", query,
                "answer", answer
        );
    }
}