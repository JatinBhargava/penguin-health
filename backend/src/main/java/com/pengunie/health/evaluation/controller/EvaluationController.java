package com.pengunie.health.evaluation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pengunie.health.evaluation.service.RetrievalEvaluator;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {

    private final RetrievalEvaluator retrievalEvaluator;

    public EvaluationController(
            RetrievalEvaluator retrievalEvaluator) {
        this.retrievalEvaluator = retrievalEvaluator;
    }

    @PostMapping("/retrieval")
    public ResponseEntity<String> evaluate()
            throws Exception {

        retrievalEvaluator.run();

        return ResponseEntity.ok(
                "Retrieval evaluation completed"
        );
    }
}
