CREATE TABLE documents (
    id UUID PRIMARY KEY,

    user_id VARCHAR(100) NOT NULL,

    file_name VARCHAR(255) NOT NULL,

    content_type VARCHAR(100) NOT NULL,

    file_size BIGINT NOT NULL,

    storage_path VARCHAR(500) NOT NULL,

    document_type VARCHAR(100),

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_documents_user_id
    ON documents(user_id);

CREATE INDEX idx_documents_status
    ON documents(status);