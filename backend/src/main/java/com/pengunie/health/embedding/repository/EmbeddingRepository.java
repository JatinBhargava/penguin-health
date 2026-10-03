package com.pengunie.health.embedding.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmbeddingRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmbeddingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveEmbedding(
            UUID chunkId,
            List<Float> embedding) {

        String vector = embedding.toString()
                .replace("[", "[")
                .replace("]", "]");

        jdbcTemplate.update(
                """
                UPDATE document_chunks
                SET embedding = ?::vector
                WHERE id = ?
                """,
                vector,
                chunkId
        );
    }
}