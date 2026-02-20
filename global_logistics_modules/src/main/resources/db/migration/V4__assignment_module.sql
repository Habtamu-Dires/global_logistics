--- shipment assignment
CREATE TABLE shipment_assignment (
     id BIGSERIAL PRIMARY KEY,

     public_id UUID UNIQUE NOT NULL,

     version BIGINT DEFAULT 0 NOT NULL,

     shipment_id UUID NOT NULL,
     driver_id UUID NOT NULL,

     status VARCHAR(40) NOT NULL,

     agreed_price NUMERIC(19,2) NOT NULL,

     start_location TEXT,
     start_latitude DOUBLE PRECISION,
     start_longitude DOUBLE PRECISION,

     assigned_by UUID NOT NULL,
     assigned_at TIMESTAMP NOT NULL,

     loaded_at TIMESTAMP WITHOUT TIME ZONE,
     started_at TIMESTAMP WITHOUT TIME ZONE,

     offloaded_at TIMESTAMP WITHOUT TIME ZONE,
     grn_generated_at TIMESTAMP WITHOUT TIME ZONE,
     consignor_confirmed_at TIMESTAMP WITHOUT TIME ZONE,

     cancelled_at TIMESTAMP WITHOUT TIME ZONE,
     cancel_reason TEXT
);

CREATE INDEX idx_assignment_public_id
    ON shipment_assignment(public_id);

CREATE INDEX idx_assignment_shipment
    ON shipment_assignment(shipment_id);

CREATE INDEX idx_assignment_driver
    ON shipment_assignment(driver_id);

----- assignment status history
CREATE TABLE shipment_assignment_status_history (
    id UUID PRIMARY KEY,
    assignment_id BIGSERIAL NOT NULL,

    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,

    changed_by UUID,
    actor_type VARCHAR(30) NOT NULL,

    remark TEXT,
    changed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_assignment_history
        FOREIGN KEY (assignment_id)
            REFERENCES shipment_assignment(id)
);

CREATE INDEX idx_assignment_history_assignment
    ON shipment_assignment_status_history(assignment_id);


--- gdn
create table gdn (
                     id                      bigserial primary key,

    -- 🔐 external reference
                     public_id               uuid        not null unique,

    -- 🧾 business identity
                     document_number         varchar(50) not null unique,

    -- 🔗 aggregate relations (internal use only)
                     assignment_id           bigint    not null,
                     shipment_id             uuid      not null,

    -- 📌 lifecycle
                     status                  varchar(20) not null, -- DRAFT, ISSUED, VOID
                     issued_at               timestamp,
                     issued_by               uuid,

    -- 👤 consignor snapshot
                     consignor_name          varchar(150) not null,
                     consignor_contact       varchar(100),

    -- 👤 consignee snapshot
                     consignee_name          varchar(150) not null,
                     consignee_contact       varchar(100),

    -- 🚚 transport snapshot
                     driver_name             varchar(150) not null,
                     driver_license_no       varchar(100),

                     vehicle_type            varchar(100) not null,
                     vehicle_plate_no        varchar(50) not null,

    -- 📦 goods snapshot
                     goods_type              varchar(100),
                     quantity                numeric(19,4),
                     weight                  varchar(100),
                     volume                  varchar(100),
                     goods_description       text        not null,
                     packaging_type          varchar(100),

    -- 📍 logistics snapshot
                     loading_location        varchar(255) not null,
                     loading_date            date        not null,
                     offloading_location    varchar(255) not null,

    -- 📝 operational notes
                     remarks                 text,

    -- 🔍 verification
                     qr_code_value           varchar(255),

    -- 🧑‍💼 audit
                     voided_by              uuid      not null,
                     void_reason            text,
                     voided_at              timestamp not null,

    --- fk
    CONSTRAINT fk_gdn_assignment FOREIGN KEY(assignment_id)
        REFERENCES shipment_assignment(id)

);
--- unique index
CREATE UNIQUE INDEX idx_unique_assignment_for_gdn
    ON gdn (assignment_id)
    WHERE (status != 'VOID');

---- search indexes
create index idx_gdn_document_number on gdn(document_number);
create index idx_gdn_public_id on gdn(public_id);
create index idx_gdn_assignment on gdn(assignment_id);
create index idx_gdn_shipment on gdn(shipment_id);
create index idx_gdn_status on gdn(status);

--- document sequence table
create table document_sequence (
       id              bigserial primary key,
       doc_type        varchar(20),   -- GDN
       year            int,
       next_value      bigint,
       unique (doc_type, year)
);


--- Good Delivery Note
create table grn (
         id                  bigserial primary key,
         public_id           uuid not null unique,
         grn_number          varchar(50) not null unique,

         assignment_id       bigint not null REFERENCES shipment_assignment(id),
         shipment_id         uuid not null,
         gdn_id              bigint not null REFERENCES gdn(id),

         received_quantity   int,
         received_weight     varchar(50),
         received_volume     varchar(50),

         damage_quantity     int,
         shortage_quantity   int,

         condition_note      text,

         received_by         uuid,
         received_at         timestamp,

         voided_by           uuid,
         void_reason         text,
         voided_at           timestamp,

         created_at          timestamp not null

);




