--- driver negotiation table
CREATE TABLE driver_negotiation (
    id BIGSERIAL PRIMARY KEY,

    public_id UUID UNIQUE NOT NULL,

    shipment_id UUID NOT NULL,
    driver_id UUID NOT NULL,

    status VARCHAR(40) NOT NULL,

    final_agreed_price NUMERIC(19,2),

    start_location TEXT,
    start_latitude DOUBLE PRECISION,
    start_longitude DOUBLE PRECISION,

    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL

);

CREATE INDEX  idx_driver_negotiation_public_id
    ON driver_negotiation(public_id);

CREATE INDEX idx_driver_negotiation_shipment
    ON driver_negotiation(shipment_id);

CREATE INDEX idx_driver_negotiation_driver
    ON driver_negotiation(driver_id);


--- driver offer
CREATE TABLE driver_offer (
      id BIGSERIAL PRIMARY KEY,

      negotiation_id BIGINT NOT NULL REFERENCES driver_negotiation(id),

      round INT NOT NULL,
      price_amount NUMERIC(19,2) NOT NULL,

      offered_by UUID NOT NULL,
      offered_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

      reason TEXT
);

CREATE INDEX idx_driver_offer_negotiation
    ON driver_offer(negotiation_id);

