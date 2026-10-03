package com.pengunie.health.evaluation.dto;

public record RetrievalEvalCase(
    String question,
    String expectedText,
    int expectedChunk
) {}