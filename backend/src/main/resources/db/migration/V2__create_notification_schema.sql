CREATE TABLE notifications (

    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    channel VARCHAR(20) NOT NULL,

    recipient VARCHAR(255) NOT NULL,

    subject VARCHAR(255),

    message VARCHAR(5000) NOT NULL,

    status VARCHAR(20) NOT NULL,

    failure_reason VARCHAR(1000),

    sent_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE INDEX idx_notification_user_id
    ON notifications(user_id);

CREATE INDEX idx_notification_status
    ON notifications(status);

CREATE INDEX idx_notification_created_at
    ON notifications(created_at);