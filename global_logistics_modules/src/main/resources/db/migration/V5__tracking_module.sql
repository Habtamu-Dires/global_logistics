--- tracking table
CREATE TABLE shipment_assignment_tracking (
    id BIGSERIAL PRIMARY KEY,

    assignment_id uuid NOT NULL,

    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,

    accuracy DOUBLE PRECISION,
    speed DOUBLE PRECISION,

    recorded_at TIMESTAMP NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tracking_assignment_time
    ON shipment_assignment_tracking (assignment_id, recorded_at);
