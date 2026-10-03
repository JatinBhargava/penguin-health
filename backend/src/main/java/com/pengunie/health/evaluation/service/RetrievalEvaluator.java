package com.pengunie.health.evaluation.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.pengunie.health.evaluation.dto.RetrievalEvalCase;
import com.pengunie.health.search.dto.SearchResult;
import com.pengunie.health.search.service.SemanticSearchService;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class RetrievalEvaluator {

    private final SemanticSearchService searchService;
    private final ObjectMapper objectMapper;

    public RetrievalEvaluator(
            SemanticSearchService searchService,
            ObjectMapper objectMapper) {

        this.searchService = searchService;
        this.objectMapper = objectMapper;
    }

public void run() throws Exception {

    ClassPathResource resource =
            new ClassPathResource("evals/retrieval-eval.json");

    try (InputStream inputStream = resource.getInputStream()) {

        List<RetrievalEvalCase> cases =
                objectMapper.readValue(
                        inputStream,
                        new TypeReference<>() {}
                );

        int passed = 0;

        for (RetrievalEvalCase evalCase : cases) {

            List<SearchResult> results =
                    searchService.search(
                            evalCase.question(),
                            3
                    );

            // DEBUG: print retrieved chunks
            System.out.println(
                    "\nQUESTION: " + evalCase.question()
            );

            for (SearchResult result : results) {

                System.out.println(
                        "Chunk: " + result.chunkIndex()
                                + " | Distance: "
                                + result.distance()
                );

                System.out.println(
                        "Content: " + result.content()
                );

                System.out.println(
                        "--------------------------------"
                );
            }

            boolean found = results.stream()
                    .anyMatch(result ->
                            result.content()
                                    .contains(evalCase.expectedText())
                    );

            if (found) {
                passed++;

                System.out.println(
                        "PASS: " + evalCase.question()
                );

            } else {

                System.out.println(
                        "FAIL: " + evalCase.question()
                );
            }
        }

        System.out.println(
                "\nRetrieval accuracy: "
                        + passed
                        + "/"
                        + cases.size()
        );
    }
}
}