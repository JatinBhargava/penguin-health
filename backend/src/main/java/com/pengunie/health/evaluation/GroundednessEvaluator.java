package com.pengunie.health.evaluation;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.pengunie.health.rag.service.RagAnswerService;
import com.pengunie.health.rag.service.RagResponse;

import org.springframework.core.io.ClassPathResource;

import java.util.List;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class GroundednessEvaluator {

    private final OpenAIClient client;
    private final RagAnswerService ragAnswerService;
    private final ObjectMapper objectMapper;

    public GroundednessEvaluator(
            @Value("${openai.api-key}") String apiKey,
            RagAnswerService ragAnswerService,
            ObjectMapper objectMapper) {

        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        this.ragAnswerService = ragAnswerService;
            this.objectMapper = objectMapper;
    }

    public GroundednessResult evaluate(
            String question,
            int topK) {

        RagResponse response
                = ragAnswerService.answerWithContext(
                        question,
                        topK
                );

        String evidence = response.results()
                .stream()
                .map(result
                        -> "[Chunk " + result.chunkIndex() + "]\n"
                + result.content()
                )
                .reduce(
                        "",
                        (a, b) -> a + "\n\n" + b
                );

        String prompt = """
                You are evaluating whether an AI-generated answer
                is grounded in the provided evidence.

                Question:
                %s

                Retrieved Evidence:
                %s

                Generated Answer:
                %s

                Evaluate every factual claim in the generated answer.

                A claim is grounded only if it is supported by
                the retrieved evidence.

                Return ONLY valid JSON:

                {
                  "grounded": true,
                  "score": 1.0,
                  "unsupportedClaims": [],
                  "explanation": "All claims are supported."
                }

                Rules:

                - grounded must be true or false.
                - score must be between 0.0 and 1.0.
                - unsupportedClaims must contain unsupported
                  factual claims.
                - Do not use outside knowledge.
                - Do not assume information that is not present
                  in the evidence.
                """.formatted(
                question,
                evidence,
                response.answer()
        );

        ChatCompletionCreateParams params
                = ChatCompletionCreateParams.builder()
                        .model("gpt-4o-mini")
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion
                = client.chat()
                        .completions()
                        .create(params);

        String judgeResponse
                = completion.choices()
                        .get(0)
                        .message()
                        .content()
                        .orElse("");

        return parseResult(judgeResponse);
    }

    private GroundednessResult parseResult(String json) {

        try {
            return objectMapper.readValue(
                    json,
                    GroundednessResult.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse groundedness evaluation: "
                    + json,
                    e
            );
        }
    }

    public String evaluateAll() {

        try {
            ClassPathResource resource
                    = new ClassPathResource("evals/rag-eval.json");

            InputStream inputStream = resource.getInputStream();

            List<RagEvalCase> testCases
                    = objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<RagEvalCase>>() {
                    }
                    );

            int groundedCount = 0;

            StringBuilder output = new StringBuilder();

            output.append("=== Groundedness Evaluation ===\n\n");

            for (RagEvalCase testCase : testCases) {

                GroundednessResult result
                        = evaluate(testCase.question(), 3);

                if (result.grounded()) {
                    groundedCount++;
                }

                output.append("Question: ")
                        .append(testCase.question())
                        .append("\n");

                output.append("Grounded: ")
                        .append(result.grounded())
                        .append("\n");

                output.append("Score: ")
                        .append(result.score())
                        .append("\n");

                output.append("Unsupported claims: ")
                        .append(result.unsupportedClaims())
                        .append("\n");

                output.append("Explanation: ")
                        .append(result.explanation())
                        .append("\n\n");
            }

            output.append("==============================\n");

            output.append("Groundedness accuracy: ")
                    .append(groundedCount)
                    .append("/")
                    .append(testCases.size())
                    .append("\n");

            return output.toString();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Groundedness evaluation failed",
                    e
            );
        }
    }
}
