CREATE TABLE customer_documents (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES users(id),
    inquiry_id BIGINT REFERENCES insurance_inquiries(id),
    document_type VARCHAR(40) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    content BYTEA NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT customer_documents_scope_check CHECK ((inquiry_id IS NULL) OR (document_type IN ('LOGBOOK', 'VALUATION_REPORT')))
);
CREATE INDEX idx_customer_documents_customer_type ON customer_documents(customer_id, document_type);
CREATE INDEX idx_customer_documents_inquiry ON customer_documents(inquiry_id);
