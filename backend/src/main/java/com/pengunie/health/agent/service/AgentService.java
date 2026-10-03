package com.pengunie.health.agent.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseFunctionToolCall;
import com.openai.models.responses.ResponseInputItem;
import com.pengunie.health.health.event.entity.HealthEvent;
import com.pengunie.health.health.event.tool.HealthEventSearchTool;
import com.pengunie.health.health.event.tool.HealthHistoryTool;
import com.pengunie.health.health.event.tool.LatestHealthEventTool;
import com.pengunie.health.rag.tool.RagSearchTool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AgentService {

    private static final int MAX_ITERATIONS = 5;
    private static final int DEFAULT_TOP_K = 3;

    private final OpenAIClient client;

    private final HealthEventSearchTool healthEventSearchTool;
    private final LatestHealthEventTool latestHealthEventTool;
    private final HealthHistoryTool healthHistoryTool;
    private final RagSearchTool ragSearchTool;

    public AgentService(
            @Value("${openai.api-key}") String apiKey,
            HealthEventSearchTool healthEventSearchTool,
            LatestHealthEventTool latestHealthEventTool,
            HealthHistoryTool healthHistoryTool,
            RagSearchTool ragSearchTool) {

        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        this.healthEventSearchTool = healthEventSearchTool;
        this.latestHealthEventTool = latestHealthEventTool;
        this.healthHistoryTool = healthHistoryTool;
        this.ragSearchTool = ragSearchTool;
    }

    public String ask(String question) {

        List<ResponseInputItem> inputs = new ArrayList<>();

        inputs.add(
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .addInputTextContent(question)
                                .role(ResponseInputItem.Message.Role.USER)
                                .build()
                )
        );

        ResponseCreateParams.Builder params =
                ResponseCreateParams.builder()
                        .model(ChatModel.GPT_4O_MINI)
                        .instructions("""
                                You are Pengunie Health.

                                You help users understand information contained
                                in their uploaded health records.

                                Rules:
                                - Use tools when health-record information is needed.
                                - Never invent health measurements.
                                - Prefer structured health-event tools for known measurements.
                                - Use RAG search when information must be retrieved from
                                  the original uploaded documents.
                                - Do not diagnose medical conditions.
                                - Do not claim that a result is clinically normal or abnormal
                                  unless the provided reference range explicitly supports that
                                  description.
                                - Keep answers concise and factual.
                                """)
                        .addTool(HealthEventSearchFunction.class)
                        .addTool(LatestHealthEventFunction.class)
                        .addTool(HealthHistoryFunction.class)
                        .addTool(RagSearchFunction.class)
                        .maxOutputTokens(1000);

        for (int iteration = 0;
             iteration < MAX_ITERATIONS;
             iteration++) {

            params.input(
                    ResponseCreateParams.Input.ofResponse(inputs)
            );

            var response =
                    client.responses().create(params.build());

            boolean hasToolCall = false;

            for (var item : response.output()) {

                if (!item.isFunctionCall()) {
                    continue;
                }

                hasToolCall = true;

                ResponseFunctionToolCall functionCall =
                        item.asFunctionCall();

                // Preserve the model's tool call.
                inputs.add(
                        ResponseInputItem.ofFunctionCall(functionCall)
                );

                // Execute trusted backend tool.
                Object result =
                        executeTool(functionCall);

                // Send tool result back to model.
                inputs.add(
                        ResponseInputItem.ofFunctionCallOutput(
                                ResponseInputItem.FunctionCallOutput
                                        .builder()
                                        .callId(functionCall.callId())
                                        .outputAsJson(result)
                                        .build()
                        )
                );
            }

            // No tool call means the model has produced
            // its final response.
            if (!hasToolCall) {

                return extractText(response);
            }
        }

        return "I couldn't complete the request within the allowed tool-call limit.";
    }

    private Object executeTool(
            ResponseFunctionToolCall functionCall) {

        return switch (functionCall.name()) {

            case "HealthEventSearchFunction" -> {

                HealthEventSearchFunction args =
                        functionCall.arguments(
                                HealthEventSearchFunction.class
                        );

                yield healthEventSearchTool.search(
                        args.name
                );
            }

            case "LatestHealthEventFunction" -> {

                LatestHealthEventFunction args =
                        functionCall.arguments(
                                LatestHealthEventFunction.class
                        );

                Optional<HealthEvent> result =
                        latestHealthEventTool.latest(
                                args.name
                        );

                yield result.orElse(null);
            }

            case "HealthHistoryFunction" -> {

                HealthHistoryFunction args =
                        functionCall.arguments(
                                HealthHistoryFunction.class
                        );

                yield healthHistoryTool.history(
                        args.name
                );
            }

            case "RagSearchFunction" -> {

                RagSearchFunction args =
                        functionCall.arguments(
                                RagSearchFunction.class
                        );

                int topK =
                        args.topK == null
                                ? DEFAULT_TOP_K
                                : args.topK;

                yield ragSearchTool.search(
                        args.query,
                        topK
                );
            }

            default ->
                    throw new IllegalArgumentException(
                            "Unknown tool: " + functionCall.name()
                    );
        };
    }

    private String extractText(
            com.openai.models.responses.Response response) {

        return response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(output -> output.text())
                .findFirst()
                .orElse(
                        "I couldn't generate an answer."
                );
    }

    // --------------------------------------------------
    // Tool definitions
    // --------------------------------------------------

    @JsonClassDescription(
            "Searches structured health events by measurement name."
    )
    public static class HealthEventSearchFunction {

        @JsonPropertyDescription(
                "Health measurement name, such as Vitamin D, HbA1c, LDL cholesterol, or creatinine."
        )
        public String name;
    }

    @JsonClassDescription(
            "Gets the most recent structured health result for a measurement."
    )
    public static class LatestHealthEventFunction {

        @JsonPropertyDescription(
                "Health measurement name, such as Vitamin D, HbA1c, LDL cholesterol, or creatinine."
        )
        public String name;
    }

    @JsonClassDescription(
            "Gets the historical structured health results for a measurement, newest first."
    )
    public static class HealthHistoryFunction {

        @JsonPropertyDescription(
                "Health measurement name, such as Vitamin D, HbA1c, LDL cholesterol, or creatinine."
        )
        public String name;
    }

    @JsonClassDescription(
            "Searches the original uploaded health documents using semantic retrieval."
    )
    public static class RagSearchFunction {

        @JsonPropertyDescription(
                "The information to search for in the uploaded health documents."
        )
        public String query;

        @JsonPropertyDescription(
                "Number of relevant document chunks to retrieve. Usually 3."
        )
        public Integer topK;
    }
}