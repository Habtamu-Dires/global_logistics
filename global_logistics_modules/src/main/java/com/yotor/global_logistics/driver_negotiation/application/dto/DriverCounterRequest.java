package com.yotor.global_logistics.driver_negotiation.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DriverCounterRequest(
        UUID negotiationId,
        BigDecimal counterPrice,
        String reason
) {
}
