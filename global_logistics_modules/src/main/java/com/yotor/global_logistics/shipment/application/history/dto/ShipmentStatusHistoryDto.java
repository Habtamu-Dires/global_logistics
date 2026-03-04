package com.yotor.global_logistics.shipment.application.history.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentStatusHistoryDto(
        String status,
        String reason,
        UUID changedBy,
        LocalDateTime changedAt
) {
}
