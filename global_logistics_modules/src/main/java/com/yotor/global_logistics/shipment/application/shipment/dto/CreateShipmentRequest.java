package com.yotor.global_logistics.shipment.application.shipment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateShipmentRequest(
        String goodType,
        Integer quantity,
        String weight,
        String volume,
        String loadingLocation,
        String offloadingLocation,
        String route,
        String requiredVehicleType,
        int requiredVehicleNumber,
        LocalDateTime loadingDate,
        LocalDateTime deliveryDate,
        String details,
        BigDecimal price,
        String priceType
) {}
