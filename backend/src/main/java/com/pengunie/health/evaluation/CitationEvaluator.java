package com.pengunie.health.evaluation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.pengunie.health.search.dto.SearchResult;

@Service
public class CitationEvaluator {

    private static final Pattern CHUNK_PATTERN =
            Pattern.compile("\\[Chunk\\s+(\\d+)\\]");

    public boolean evaluate(
            String answer,
            List<SearchResult> results
    ) {

        Matcher matcher =
                CHUNK_PATTERN.matcher(answer);

        if (!matcher.find()) {
            return false;
        }

        int citedChunk =
                Integer.parseInt(matcher.group(1));

        return results.stream()
                .anyMatch(result ->
                        result.chunkIndex() == citedChunk
                );
    }
}