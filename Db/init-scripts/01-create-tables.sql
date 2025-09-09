\connect paymentsdb;

-- Transfer Service table
CREATE TABLE transfers (
                           id BIGSERIAL PRIMARY KEY,
                           transfer_id VARCHAR(64) NOT NULL UNIQUE,
                           from_account_id BIGINT NOT NULL,
                           to_account_id BIGINT NOT NULL,
                           amount NUMERIC(19,2) NOT NULL,
                           status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
                           idempotency_key VARCHAR(64) NOT NULL UNIQUE,
                           created_at TIMESTAMP DEFAULT NOW()
);
