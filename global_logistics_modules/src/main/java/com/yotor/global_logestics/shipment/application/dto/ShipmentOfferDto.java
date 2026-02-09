package com.yotor.global_logestics.shipment.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentOfferDto(
         int round,

         BigDecimal priceAmount,

         String requiredVehicleType,
         int requiredVehicleNumber,

         LocalDateTime loadingDate,
         LocalDateTime deliveryDate,

         String reason,
         UUID offeredBy,
         LocalDateTime offeredAt
) {}
