package com.yotor.global_logestics.shipment.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentStatusHistoryDto(
        String status,
        String reason,
        UUID changedBy,
        LocalDateTime changedAt
) {
}
