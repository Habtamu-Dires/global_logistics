--- notification table
CREATE TABLE notification (
      id              BIGSERIAL PRIMARY KEY,
      public_id       UUID        NOT NULL UNIQUE,

      receiver_id     UUID        NOT NULL,
      receiver_type   VARCHAR(20) NOT NULL,   -- ADMIN / DRIVER / CONSIGNOR

      title           VARCHAR(150) NOT NULL,
      message         TEXT         NOT NULL,

      type            VARCHAR(50)  NOT NULL,  -- ASSIGNMENT_CREATED, PAYMENT_VERIFIED, etc

      reference_type  VARCHAR(30),            -- SHIPMENT / ASSIGNMENT / PAYMENT
      reference_id    UUID,                   -- public id of target aggregate

      is_read         BOOLEAN     NOT NULL DEFAULT FALSE,
      read_at         TIMESTAMP,

      created_at      TIMESTAMP   NOT NULL
);

CREATE INDEX idx_notification_receiver_unread
    ON notification(receiver_id, is_read);

CREATE INDEX idx_notification_receiver_created
    ON notification(receiver_id, created_at DESC);