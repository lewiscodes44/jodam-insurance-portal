-- Add schedule and underwriting-verification fields for the staff-issued motor policy.
ALTER TABLE policies
    ADD COLUMN IF NOT EXISTS insurer VARCHAR(150),
    ADD COLUMN IF NOT EXISTS product VARCHAR(150),
    ADD COLUMN IF NOT EXISTS certificate_number VARCHAR(80),
    ADD COLUMN IF NOT EXISTS certificate_class VARCHAR(40),
    ADD COLUMN IF NOT EXISTS valuation_reference VARCHAR(100),
    ADD COLUMN IF NOT EXISTS valuation_date DATE,
    ADD COLUMN IF NOT EXISTS documents_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS policy_terms VARCHAR(5000);