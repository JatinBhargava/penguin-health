package com.pengunie.health.evaluation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

import com.pengunie.health.rag.service.RagAnswerService;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class RagEvaluator {

    private final RagAnswerService ragAnswerService;
    private final ObjectMapper objectMapper;

    public RagEvaluator(
            RagAnswerService ragAnswerService,
            ObjectMapper objectMapper
    ) {
        this.ragAnswerService = ragAnswerService;
        this.objectMapper = objectMapper;
    }

    public String evaluate() {

        try {
            ClassPathResource resource =
                    new ClassPathResource("evals/rag-eval.json");

            InputStream inputStream = resource.getInputStream();

            List<RagEvalCase> testCases =
                    objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<RagEvalCase>>() {}
                    );

            int passed = 0;

            StringBuilder output = new StringBuilder();

            output.append("=== RAG Evaluation ===\n\n");

            for (RagEvalCase testCase : testCases) {

                String answer =
                        ragAnswerService.answer(
                                testCase.question(),
                                3
                        );

                boolean success =
                        answer.contains(testCase.expectedAnswer());

                if (success) {
                    passed++;
                }

                output.append("Question: ")
                        .append(testCase.question())
                        .append("\n");

                output.append("Expected: ")
                        .append(testCase.expectedAnswer())
                        .append("\n");

                output.append("Actual: ")
                        .append(answer)
                        .append("\n");

                output.append("Result: ")
                        .append(success ? "PASS" : "FAIL")
                        .append("\n\n");
            }

            output.append("====================\n");
            output.append("RAG accuracy: ")
                    .append(passed)
                    .append("/")
                    .append(testCases.size())
                    .append("\n");

            return output.toString();

        } catch (Exception e) {
            throw new RuntimeException(
                    "RAG evaluation failed",
                    e
            );
        }
    }
}