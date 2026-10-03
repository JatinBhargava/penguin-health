package com.pengunie.health.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class RagAnswerService {

    private final OpenAIClient client;
    private final RagContextBuilder contextBuilder;
    private final RagPromptBuilder promptBuilder;
    private final MeterRegistry meterRegistry;

    public RagAnswerService(
            @Value("${openai.api-key}") String apiKey,
            RagContextBuilder contextBuilder,
            RagPromptBuilder promptBuilder,
            MeterRegistry meterRegistry) {

        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        this.contextBuilder = contextBuilder;
        this.promptBuilder = promptBuilder;
        this.meterRegistry = meterRegistry;
    }

    public String answer(String query, int topK) {

        Timer.Sample totalTimer
                = Timer.start(meterRegistry);

        Timer.Sample contextTimer
                = Timer.start(meterRegistry);

        var context
                = contextBuilder.build(query, topK);

        contextTimer.stop(
                Timer.builder("rag.context")
                        .description("Time spent building RAG context")
                        .register(meterRegistry)
        );

        String prompt
                = promptBuilder.build(context);

        Timer.Sample llmTimer
                = Timer.start(meterRegistry);

        ChatCompletionCreateParams params
                = ChatCompletionCreateParams.builder()
                        .model("gpt-4o-mini")
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion
                = client.chat()
                        .completions()
                        .create(params);

        llmTimer.stop(
                Timer.builder("rag.llm")
                        .description("Time spent generating RAG answer")
                        .register(meterRegistry)
        );

        totalTimer.stop(
                Timer.builder("rag.total")
                        .description("Total RAG answer time")
                        .register(meterRegistry)
        );

        return completion.choices()
                .get(0)
                .message()
                .content()
                .orElse("");
    }

    public RagResponse answerWithContext(String query, int topK) {

        var context = contextBuilder.build(query, topK);

        String prompt = promptBuilder.build(context);

        ChatCompletionCreateParams params
                = ChatCompletionCreateParams.builder()
                        .model("gpt-4o-mini")
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion
                = client.chat()
                        .completions()
                        .create(params);

        String answer = completion.choices()
                .get(0)
                .message()
                .content()
                .orElse("");

        return new RagResponse(
                answer,
                context.results()
        );
    }
}
