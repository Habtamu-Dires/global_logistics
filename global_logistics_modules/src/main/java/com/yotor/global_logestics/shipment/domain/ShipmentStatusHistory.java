package com.yotor.global_logestics.shipment.domain;

import com.yotor.global_logestics.shipment.domain.dto.ShipmentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("shipment_status_history")
public class ShipmentStatusHistory {

    @Id
    private Long id;

    private final ShipmentStatus status;
    private final UUID changedBy;
    private final String reason;
    private final LocalDateTime changedAt;

    public ShipmentStatusHistory(
            ShipmentStatus status,
            UUID changedBy,
            String reason,
            LocalDateTime changedAt
    ) {
        this.status = status;
        this.changedBy = changedBy;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    // getters
}


