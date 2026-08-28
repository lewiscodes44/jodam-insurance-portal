CREATE TABLE claims (

    id BIGSERIAL PRIMARY KEY,

    claim_number VARCHAR(50) NOT NULL UNIQUE,

    policy_id BIGINT NOT NULL,

    assigned_agent_id BIGINT,

    incident_date DATE NOT NULL,

    description TEXT NOT NULL,

    claimed_amount NUMERIC(12, 2) NOT NULL,

    status VARCHAR(30) NOT NULL,

    decision_reason VARCHAR(2000),

    approved_amount NUMERIC(12, 2),

    reviewed_at TIMESTAMP,

    settled_at TIMESTAMP,

    closed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_claims_policy
        FOREIGN KEY (policy_id)
        REFERENCES policies(id),

    CONSTRAINT fk_claims_assigned_agent
        FOREIGN KEY (assigned_agent_id)
        REFERENCES users(id),

    CONSTRAINT chk_claims_claimed_amount_positive
        CHECK (claimed_amount > 0),

    CONSTRAINT chk_claims_approved_amount_non_negative
        CHECK (
            approved_amount IS NULL
            OR approved_amount >= 0
        )
);

CREATE INDEX idx_claim_policy_id
    ON claims(policy_id);

CREATE INDEX idx_claim_assigned_agent_id
    ON claims(assigned_agent_id);

CREATE INDEX idx_claim_status
    ON claims(status);

CREATE INDEX idx_claim_created_at
    ON claims(created_at);