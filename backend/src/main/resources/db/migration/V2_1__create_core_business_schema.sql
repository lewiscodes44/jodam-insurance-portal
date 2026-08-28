CREATE TABLE IF NOT EXISTS insurance_inquiries (

    id BIGSERIAL PRIMARY KEY,

    customer_id BIGINT NOT NULL,

    assigned_agent_id BIGINT,

    insurance_type VARCHAR(100) NOT NULL,

    description TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'NEW',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_inquiry_customer'
    ) THEN

        ALTER TABLE insurance_inquiries
            ADD CONSTRAINT fk_inquiry_customer
            FOREIGN KEY (customer_id)
            REFERENCES users(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_inquiry_agent'
    ) THEN

        ALTER TABLE insurance_inquiries
            ADD CONSTRAINT fk_inquiry_agent
            FOREIGN KEY (assigned_agent_id)
            REFERENCES users(id);

    END IF;

END
$$;


CREATE TABLE IF NOT EXISTS quotations (

    id BIGSERIAL PRIMARY KEY,

    inquiry_id BIGINT NOT NULL,

    agent_id BIGINT NOT NULL,

    premium_amount NUMERIC(12, 2) NOT NULL,

    coverage_details VARCHAR(5000),

    valid_until DATE NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_quotation_inquiry'
    ) THEN

        ALTER TABLE quotations
            ADD CONSTRAINT fk_quotation_inquiry
            FOREIGN KEY (inquiry_id)
            REFERENCES insurance_inquiries(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_quotation_agent'
    ) THEN

        ALTER TABLE quotations
            ADD CONSTRAINT fk_quotation_agent
            FOREIGN KEY (agent_id)
            REFERENCES users(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'quotations_inquiry_id_key'
    ) THEN

        ALTER TABLE quotations
            ADD CONSTRAINT quotations_inquiry_id_key
            UNIQUE (inquiry_id);

    END IF;

END
$$;


CREATE TABLE IF NOT EXISTS policies (

    id BIGSERIAL PRIMARY KEY,

    policy_number VARCHAR(50) NOT NULL,

    inquiry_id BIGINT NOT NULL,

    quotation_id BIGINT NOT NULL,

    customer_id BIGINT NOT NULL,

    agent_id BIGINT NOT NULL,

    insurance_type VARCHAR(100) NOT NULL,

    premium_amount NUMERIC(12, 2) NOT NULL,

    coverage_details VARCHAR(5000),

    start_date DATE NOT NULL,

    end_date DATE NOT NULL,

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    cancellation_reason VARCHAR(500),

    cancelled_at TIMESTAMP
);

DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_policy_inquiry'
    ) THEN

        ALTER TABLE policies
            ADD CONSTRAINT fk_policy_inquiry
            FOREIGN KEY (inquiry_id)
            REFERENCES insurance_inquiries(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_policy_quotation'
    ) THEN

        ALTER TABLE policies
            ADD CONSTRAINT fk_policy_quotation
            FOREIGN KEY (quotation_id)
            REFERENCES quotations(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_policy_customer'
    ) THEN

        ALTER TABLE policies
            ADD CONSTRAINT fk_policy_customer
            FOREIGN KEY (customer_id)
            REFERENCES users(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_policy_agent'
    ) THEN

        ALTER TABLE policies
            ADD CONSTRAINT fk_policy_agent
            FOREIGN KEY (agent_id)
            REFERENCES users(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'policies_inquiry_id_key'
    ) THEN

        ALTER TABLE policies
            ADD CONSTRAINT policies_inquiry_id_key
            UNIQUE (inquiry_id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'policies_quotation_id_key'
    ) THEN

        ALTER TABLE policies
            ADD CONSTRAINT policies_quotation_id_key
            UNIQUE (quotation_id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'policies_policy_number_key'
    ) THEN

        ALTER TABLE policies
            ADD CONSTRAINT policies_policy_number_key
            UNIQUE (policy_number);

    END IF;

END
$$;


CREATE TABLE IF NOT EXISTS payments (

    id BIGSERIAL PRIMARY KEY,

    policy_id BIGINT NOT NULL,

    amount NUMERIC(12, 2) NOT NULL,

    phone_number VARCHAR(20) NOT NULL,

    transaction_reference VARCHAR(100),

    checkout_request_id VARCHAR(100),

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_payment_policy'
    ) THEN

        ALTER TABLE payments
            ADD CONSTRAINT fk_payment_policy
            FOREIGN KEY (policy_id)
            REFERENCES policies(id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'payments_checkout_request_id_key'
    ) THEN

        ALTER TABLE payments
            ADD CONSTRAINT payments_checkout_request_id_key
            UNIQUE (checkout_request_id);

    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'payments_transaction_reference_key'
    ) THEN

        ALTER TABLE payments
            ADD CONSTRAINT payments_transaction_reference_key
            UNIQUE (transaction_reference);

    END IF;

END
$$;


CREATE INDEX IF NOT EXISTS idx_inquiry_customer_id
    ON insurance_inquiries(customer_id);

CREATE INDEX IF NOT EXISTS idx_inquiry_assigned_agent_id
    ON insurance_inquiries(assigned_agent_id);

CREATE INDEX IF NOT EXISTS idx_inquiry_status
    ON insurance_inquiries(status);

CREATE INDEX IF NOT EXISTS idx_inquiry_created_at
    ON insurance_inquiries(created_at);


CREATE INDEX IF NOT EXISTS idx_quotation_agent_id
    ON quotations(agent_id);

CREATE INDEX IF NOT EXISTS idx_quotation_created_at
    ON quotations(created_at);


CREATE INDEX IF NOT EXISTS idx_policy_customer_id
    ON policies(customer_id);

CREATE INDEX IF NOT EXISTS idx_policy_agent_id
    ON policies(agent_id);

CREATE INDEX IF NOT EXISTS idx_policy_status
    ON policies(status);

CREATE INDEX IF NOT EXISTS idx_policy_created_at
    ON policies(created_at);


CREATE INDEX IF NOT EXISTS idx_payment_policy_id
    ON payments(policy_id);

CREATE INDEX IF NOT EXISTS idx_payment_status
    ON payments(status);

CREATE INDEX IF NOT EXISTS idx_payment_created_at
    ON payments(created_at);