--- app_user table
CREATE TABLE app_user (
      id BIGSERIAL PRIMARY KEY,
      public_id UUID NOT NULL UNIQUE,

      phone VARCHAR(20) NOT NULL UNIQUE,
      password_hash VARCHAR(255) NOT NULL,

      role VARCHAR(20) NOT NULL,
      status VARCHAR(20) NOT NULL,

      first_name VARCHAR(100),
      last_name VARCHAR(100),
      national_id VARCHAR(50),
      profile_pic TEXT,

      phone_verified BOOLEAN NOT NULL DEFAULT FALSE,

      created_at TIMESTAMP NOT NULL DEFAULT now(),
      updated_at TIMESTAMP NOT NULL DEFAULT now(),
      remark TEXT
);

CREATE INDEX ix_user_public_id
    ON app_user(public_id);

-- driver profile
CREATE TABLE driver_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,

    licence_number VARCHAR(100) NOT NULL,
    licence_document TEXT,
    region VARCHAR(255),
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_driver_user
        FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- consignor profile
CREATE TABLE consignor_profile (
   id BIGSERIAL PRIMARY KEY,
   user_id BIGINT NOT NULL UNIQUE,

   business_name VARCHAR(255),
   trade_licence VARCHAR(255),

   CONSTRAINT fk_consignor_user
       FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- vehicle
CREATE TABLE vehicle (
     id BIGSERIAL PRIMARY KEY,
     public_id UUID NOT NULL UNIQUE,

     driver_id BIGINT NOT NULL,

     plate_number VARCHAR(50) NOT NULL UNIQUE,
     type VARCHAR(50) NOT NULL,
     insurance_doc TEXT,
     status VARCHAR(20) NOT NULL,
     details TEXT,
     photo TEXT,

     created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),

     CONSTRAINT fk_vehicle_driver
         FOREIGN KEY (driver_id) REFERENCES driver_profile(id)
);

-- refresh_token
CREATE TABLE refresh_token(
      id BIGSERIAL PRIMARY KEY,
      user_public_id UUID NOT NULL,

      token_hash TEXT NOT NULL,
      issued_at TIMESTAMP NOT NULL,
      expires_at TIMESTAMP NOT NULL,
      revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX ux_refresh_token_hash
    ON refresh_token(token_hash);

CREATE INDEX ix_refresh_token_user
    ON refresh_token(user_public_id);

--- otp_verification
CREATE TABLE otp_verification (
      id BIGSERIAL PRIMARY KEY,
      phone VARCHAR(20) NOT NULL,
      code VARCHAR(20) NOT NULL,
      resend_count INT DEFAULT 0,
      last_sent_at TIMESTAMP WITHOUT TIME ZONE,
      expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
      attempts INT NOT NULL,
      status VARCHAR(20) NOT NULL
);

CREATE INDEX ix_otp_phone
    ON otp_verification(phone);

--- otp_rate_limit
CREATE TABLE otp_rate_limit (
    id BIGSERIAL PRIMARY KEY ,
    phone VARCHAR(20) NOT NULL UNIQUE ,

    window_start DATE NOT NULL,
    sent_count INT NOT NULL,

    violation_count INT NOT NULL DEFAULT 0,

    blocked_until TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);






