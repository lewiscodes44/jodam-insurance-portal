-- Expand the inquiry and quotation models for the staff processing workflow.

ALTER TABLE quotations
    ADD COLUMN IF NOT EXISTS insurer VARCHAR(150),
    ADD COLUMN IF NOT EXISTS product VARCHAR(150),
    ADD COLUMN IF NOT EXISTS basic_premium NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS training_levy NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS phcf_levy NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS stamp_duty NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS other_charges NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS total_payable NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS quote_reference VARCHAR(50),
    ADD COLUMN IF NOT EXISTS proposed_start_date DATE,
    ADD COLUMN IF NOT EXISTS proposed_end_date DATE,
    ADD COLUMN IF NOT EXISTS excess VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS special_terms VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS agent_notes VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS status VARCHAR(20),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS sent_at TIMESTAMP;

UPDATE quotations
SET insurer = COALESCE(NULLIF(insurer, ''), 'Not specified')
WHERE insurer IS NULL OR insurer = '';

UPDATE quotations
SET product = COALESCE(NULLIF(product, ''), 'Not specified')
WHERE product IS NULL OR product = '';

UPDATE quotations
SET basic_premium = COALESCE(basic_premium, premium_amount),
    training_levy = COALESCE(training_levy, 0),
    phcf_levy = COALESCE(phcf_levy, 0),
    stamp_duty = COALESCE(stamp_duty, 0),
    other_charges = COALESCE(other_charges, 0),
    total_payable = COALESCE(total_payable, premium_amount),
    special_terms = COALESCE(special_terms, coverage_details),
    status = COALESCE(status, 'SENT'),
    updated_at = COALESCE(updated_at, created_at),
    quote_reference = COALESCE(
        quote_reference,
        'JODAM-Q-' || LPAD(id::text, 6, '0')
    );

ALTER TABLE quotations
    ALTER COLUMN insurer SET NOT NULL,
    ALTER COLUMN product SET NOT NULL,
    ALTER COLUMN basic_premium SET NOT NULL,
    ALTER COLUMN training_levy SET DEFAULT 0,
    ALTER COLUMN training_levy SET NOT NULL,
    ALTER COLUMN phcf_levy SET DEFAULT 0,
    ALTER COLUMN phcf_levy SET NOT NULL,
    ALTER COLUMN stamp_duty SET DEFAULT 0,
    ALTER COLUMN stamp_duty SET NOT NULL,
    ALTER COLUMN other_charges SET DEFAULT 0,
    ALTER COLUMN other_charges SET NOT NULL,
    ALTER COLUMN total_payable SET NOT NULL,
    ALTER COLUMN quote_reference SET NOT NULL,
    ALTER COLUMN status SET DEFAULT 'DRAFT',
    ALTER COLUMN status SET NOT NULL,
    ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN updated_at SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS quotations_quote_reference_key
    ON quotations(quote_reference);

CREATE INDEX IF NOT EXISTS idx_quotation_status
    ON quotations(status);

-- New workflow statuses are used for all new inquiries. Legacy values are
-- translated so existing records continue at the equivalent new stage.
UPDATE insurance_inquiries
SET status = CASE status
    WHEN 'QUOTED' THEN 'QUOTATION_SENT'
    WHEN 'ACCEPTED' THEN 'CUSTOMER_ACCEPTED'
    WHEN 'REJECTED' THEN 'CUSTOMER_DECLINED'
    WHEN 'CONVERTED' THEN 'POLICY_PENDING_PAYMENT'
    ELSE status
END
WHERE status IN ('QUOTED', 'ACCEPTED', 'REJECTED', 'CONVERTED');