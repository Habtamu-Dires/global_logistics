--shipment table
CREATE TABLE shipment (
      id BIGSERIAL PRIMARY KEY,

      public_id UUID NOT NULL UNIQUE,
      consignor_id UUID NOT NULL,

      current_status VARCHAR(40) NOT NULL,

      price_amount NUMERIC(14,2) NOT NULL,
      price_type VARCHAR(10) NOT NULL,
      price_currency VARCHAR(3) NOT NULL,

      good_type VARCHAR(100) NOT NULL,
      quantity INT NOT NULL CHECK ( quantity > 0 ),
      weight VARCHAR(50),
      volume VARCHAR(50),

      loading_location VARCHAR(255) NOT NULL,
      offloading_location VARCHAR(255) NOT NULL,
      route VARCHAR(255) NOT NULL ,

      required_vehicle_type VARCHAR(50) NOT NULL,
      required_vehicle_number INT NOT NULL CHECK ( quantity > 0),

      loading_date TIMESTAMP,
      delivery_date TIMESTAMP,

      details TEXT,

      created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
      updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_shipment_status ON shipment(current_status, created_at DESC);
CREATE INDEX idx_shipment_publicId ON shipment(public_id);

--shipment offer
CREATE TABLE shipment_offer (
        id BIGSERIAL PRIMARY KEY,

        shipment_id BIGINT NOT NULL REFERENCES shipment(id),

        round INT NOT NULL,

        price_amount NUMERIC(14,2) NOT NULL,

        required_vehicle_type VARCHAR(50) NOT NULL,
        required_vehicle_number INT NOT NULL CHECK (required_vehicle_number > 0),

        loading_date TIMESTAMP NOT NULL,
        delivery_date TIMESTAMP NOT NULL,

        reason TEXT,
        offered_by UUID NOT NULL,
        offered_at TIMESTAMP NOT NULL,

        UNIQUE (shipment_id, round)
);
CREATE INDEX idx_shipment_offer_shipment_id ON shipment_offer(shipment_id);

--- shipment status table
CREATE TABLE shipment_status_history (
     id BIGSERIAL PRIMARY KEY,

     shipment_id BIGINT NOT NULL REFERENCES shipment(id),

     status VARCHAR(40) NOT NULL,
     changed_by UUID NOT NULL,
     reason TEXT,
     changed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_shipment_status_history_shipment_id ON shipment_status_history(shipment_id);
