package com.pengunie.health.health.event.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.pengunie.health.health.event.dto.ExtractedHealthEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthEventExtractionService {

    private final OpenAIClient client;
    private final ObjectMapper objectMapper;

    public HealthEventExtractionService(
            @Value("${openai.api-key}") String apiKey,
            ObjectMapper objectMapper) {

        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        this.objectMapper = objectMapper;
    }

    public List<ExtractedHealthEvent> extract(String documentText) {

String prompt = """
        Extract structured health events from the following health report.

        The report is provided as numbered chunks.

        Return ONLY valid JSON.
        Return a JSON array.

        Each object must contain:
        - eventType
        - name
        - value
        - unit
        - referenceRange
        - observedAt
        - sourceChunkIndex

        Rules:
        - Extract only facts explicitly present in the document.
        - Do not infer missing values.
        - Do not diagnose the patient.
        - Do not provide medical advice.
        - For laboratory results use eventType = "LAB_RESULT".
        - observedAt should be the report's collection/observation date.
        - If a field is unavailable, use null.
        - sourceChunkIndex must be the number of the chunk where the
          extracted fact appears.
        - Do not invent a sourceChunkIndex.
        - sourceChunkIndex must be an integer from the provided chunks.

        Example:

        [
          {
            "eventType": "LAB_RESULT",
            "name": "Vitamin D (25-OH)",
            "value": "21",
            "unit": "ng/mL",
            "referenceRange": "30 - 100",
            "observedAt": "2026-09-28",
            "sourceChunkIndex": 1
          }
        ]

        HEALTH REPORT:
        ---
        %s
        ---
        """.formatted(documentText);

        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model("gpt-4o-mini")
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion = client.chat()
                .completions()
                .create(params);

        String response = completion.choices()
                .get(0)
                .message()
                .content()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "OpenAI returned an empty response"
                        ));

        try {
            return objectMapper.readValue(
                    extractJsonArray(response),
                    new TypeReference<List<ExtractedHealthEvent>>() {}
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse extracted health events: " + response,
                    e
            );
        }
    }

    // The model sometimes wraps the JSON in ```json fences or adds prose around it.
    private String extractJsonArray(String response) {

        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');

        if (start == -1 || end < start) {
            return response;
        }

        return response.substring(start, end + 1);
    }
}