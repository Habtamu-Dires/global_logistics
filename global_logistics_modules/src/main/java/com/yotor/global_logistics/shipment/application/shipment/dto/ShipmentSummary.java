package com.yotor.global_logistics.shipment.application.shipment.dto;

import com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ShipmentSummary(
        UUID publicId,
        String goodType,
        String loadingLocation,
        String offloadingLocation,
        BigDecimal priceAmount,
        String priceCurrency,
        String requiredVehicleType,
        int requiredVehicleNumber,
        Instant loadingDate,
        ShipmentStatus currentStatus,
        Instant createdAt
) {}