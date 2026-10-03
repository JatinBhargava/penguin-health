CREATE TABLE health_events (
    id UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,

    document_id UUID,
    event_type VARCHAR(50) NOT NULL,

    name VARCHAR(255) NOT NULL,
    value VARCHAR(255),
    unit VARCHAR(100),
    reference_range VARCHAR(255),

    observed_at DATE,

    source_chunk_id UUID,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_health_events_document
        FOREIGN KEY (document_id)
        REFERENCES documents(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_health_events_source_chunk
        FOREIGN KEY (source_chunk_id)
        REFERENCES document_chunks(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_health_events_user_id
    ON health_events(user_id);

CREATE INDEX idx_health_events_observed_at
    ON health_events(observed_at);

CREATE INDEX idx_health_events_document_id
    ON health_events(document_id);

CREATE INDEX idx_health_events_event_type
    ON health_events(event_type);