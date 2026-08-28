ALTER TABLE insurance_inquiries
    ADD COLUMN IF NOT EXISTS application_data TEXT;
