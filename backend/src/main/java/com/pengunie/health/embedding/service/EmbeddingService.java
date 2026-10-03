package com.pengunie.health.embedding.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.embeddings.EmbeddingCreateParams;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class EmbeddingService {

    private final OpenAIClient client;
    private final MeterRegistry meterRegistry;

public EmbeddingService(
        @Value("${openai.api-key}") String apiKey,
        MeterRegistry meterRegistry) {

    this.client = OpenAIOkHttpClient.builder()
            .apiKey(apiKey)
            .build();

    this.meterRegistry = meterRegistry;
}

public List<Float> createEmbedding(String text) {

    Timer.Sample timer =
            Timer.start(meterRegistry);

    try {

        EmbeddingCreateParams params =
                EmbeddingCreateParams.builder()
                        .model("text-embedding-3-small")
                        .input(text)
                        .build();

        return client.embeddings()
                .create(params)
                .data()
                .get(0)
                .embedding();

    } finally {

        timer.stop(
                Timer.builder("rag.embedding")
                        .description("Time spent creating query embeddings")
                        .register(meterRegistry)
        );
    }
}
}