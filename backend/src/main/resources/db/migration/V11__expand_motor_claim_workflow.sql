ALTER TABLE claims
    ADD COLUMN IF NOT EXISTS claim_type VARCHAR(40) NOT NULL DEFAULT 'ACCIDENT_OWN_DAMAGE',
    ADD COLUMN IF NOT EXISTS incident_time TIME,
    ADD COLUMN IF NOT EXISTS incident_location VARCHAR(500),
    ADD COLUMN IF NOT EXISTS driver_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS driver_license_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS third_party_details VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS witness_details VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS assessment_notes VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS repair_authority_reference VARCHAR(100),
    ADD COLUMN IF NOT EXISTS settlement_reference VARCHAR(100),
    ADD COLUMN IF NOT EXISTS information_request VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS information_requested_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS assessed_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS claim_documents (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    content BYTEA NOT NULL,
    uploaded_by VARCHAR(100) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_claim_documents_claim_id ON claim_documents(claim_id);
