package com.pengunie.health.rag.service;

import org.springframework.stereotype.Service;

import com.pengunie.health.rag.model.RagContext;
import com.pengunie.health.search.dto.SearchResult;

@Service
public class RagPromptBuilder {

    public String build(RagContext context) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are Pengunie Health.

                Answer the user's question using ONLY the provided
                health-document context.

                Rules:
                - Do not invent facts or measurements.
                - Do not use information that is not present in the context.
                - If the answer cannot be found in the context, say:
                  "I couldn't find that information in the uploaded documents."
                - Keep the answer concise and factual.
                - For each factual statement, include the relevant chunk
                  reference in the format [Chunk X].

                User question:
                """);

        prompt.append(context.query());

        prompt.append("\n\nDocument context:\n");

        for (SearchResult result : context.results()) {

            prompt.append("\n--- Chunk ")
                    .append(result.chunkIndex())
                    .append(" ---\n");

            prompt.append(result.content());

            prompt.append("\n");
        }

        return prompt.toString();
    }
}