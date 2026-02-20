--- driver payment table
CREATE TABLE assignment_finance (
        id BIGSERIAL PRIMARY KEY,

        public_id UUID NOT NULL UNIQUE,
        shipment_id UUID NOT NULL UNIQUE,

        agreed_amount NUMERIC(19,2) NOT NULL,
        paid_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
        remaining_amount NUMERIC(19,2) NOT NULL,

        status VARCHAR(30) NOT NULL, -- UNPAID | PARTIALLY_PAID | PAID

        created_at TIMESTAMP NOT NULL,
        updated_at TIMESTAMP
);

--- driver payment table
CREATE TABLE driver_payment (
        id BIGSERIAL PRIMARY KEY,

        public_id UUID NOT NULL UNIQUE,
        assignment_finance_id bigint NOT NULL,

        amount NUMERIC(19,2) NOT NULL,

        status VARCHAR(30) NOT NULL, -- PENDING | VERIFIED | REJECTED

        reference_no VARCHAR(100),
        slip_url TEXT,

        paid_at TIMESTAMP WITHOUT TIME ZONE,
        verified_at TIMESTAMP,
        verified_by UUID,

        voided_at TIMESTAMP WITHOUT TIME ZONE,
        voided_by UUID,
        void_reason TEXT,

        created_at TIMESTAMP NOT NULL,

        CONSTRAINT fk_shipment_payment_finance
            FOREIGN KEY (assignment_finance_id)
                REFERENCES assignment_finance(id)
);


