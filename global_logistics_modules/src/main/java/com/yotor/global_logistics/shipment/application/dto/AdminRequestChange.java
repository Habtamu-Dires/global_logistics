package com.yotor.global_logistics.shipment.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminRequestChange(
        UUID shipmentId,
        BigDecimal priceAmount,
        int requiredVehicleNumber,
        String requiredVehicleType,
        LocalDateTime loadingDate,
        LocalDateTime deliveryDate,
        String reason
) { }
