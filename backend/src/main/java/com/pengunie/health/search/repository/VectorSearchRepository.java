package com.pengunie.health.search.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pengunie.health.search.dto.SearchResult;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Repository
public class VectorSearchRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MeterRegistry meterRegistry;

    public VectorSearchRepository(
            JdbcTemplate jdbcTemplate,
            MeterRegistry meterRegistry) {

        this.jdbcTemplate = jdbcTemplate;
        this.meterRegistry = meterRegistry;
    }

    public List<SearchResult> search(
            List<Float> embedding,
            int limit) {

        Timer.Sample timer
                = Timer.start(meterRegistry);

        try {

            String vector = embedding.toString();

            return jdbcTemplate.query(
                    """
                SELECT
                    id,
                    document_id,
                    chunk_index,
                    content,
                    token_count,
                    created_at,
                    embedding <=> ?::vector AS distance
                FROM document_chunks
                WHERE embedding IS NOT NULL
                ORDER BY distance
                LIMIT ?
                """,
                    (rs, rowNum) -> new SearchResult(
                            UUID.fromString(
                                    rs.getString("id")
                            ),
                            UUID.fromString(
                                    rs.getString("document_id")
                            ),
                            rs.getInt("chunk_index"),
                            rs.getString("content"),
                            rs.getDouble("distance")
                    ),
                    vector,
                    limit
            );

        } finally {

            timer.stop(
                    Timer.builder("rag.search")
                            .description("Time spent searching pgvector")
                            .register(meterRegistry)
            );
        }
    }
}
