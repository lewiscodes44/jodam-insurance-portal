ALTER TABLE quotations
    ADD COLUMN IF NOT EXISTS customer_review_message VARCHAR(3000),
    ADD COLUMN IF NOT EXISTS review_requested_at TIMESTAMP;
