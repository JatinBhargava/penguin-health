CREATE TABLE document_chunks (
    id UUID PRIMARY KEY,

    document_id UUID NOT NULL,

    chunk_index INTEGER NOT NULL,

    content TEXT NOT NULL,

    token_count INTEGER,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document_chunks_document
        FOREIGN KEY (document_id)
        REFERENCES documents(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_document_chunks_document_id
    ON document_chunks(document_id);

CREATE UNIQUE INDEX idx_document_chunks_document_index
    ON document_chunks(document_id, chunk_index);