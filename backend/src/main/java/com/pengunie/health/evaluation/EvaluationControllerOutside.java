package com.pengunie.health.evaluation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationControllerOutside {

    private final RagEvaluator ragEvaluator;
    private final GroundednessEvaluator groundednessEvaluator;

    public EvaluationControllerOutside(
            RagEvaluator ragEvaluator,
            GroundednessEvaluator groundednessEvaluator) {

        this.ragEvaluator = ragEvaluator;
        this.groundednessEvaluator = groundednessEvaluator;
    }

    @PostMapping("/rag")
    public String evaluateRag() {
        return ragEvaluator.evaluate();
    }

    @PostMapping("/groundedness")
    public GroundednessResult evaluateGroundedness(
            @RequestParam String question) {

        return groundednessEvaluator.evaluate(
                question,
                3
        );
    }

    @PostMapping("/groundedness/all")
    public String evaluateAllGroundedness() {
        return groundednessEvaluator.evaluateAll();
    }
}
