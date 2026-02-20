package com.yotor.global_logistics.shipment.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsignorCounterReq(
        UUID shipmentId,
        BigDecimal counterPrice,
        String requiredVehicleType,
        int requiredVehicleNumber,
        LocalDateTime loadingDate,
        LocalDateTime deliveryDate,
        String reason
) {
}
