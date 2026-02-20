package com.yotor.global_logistics.shipment_finance.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentPaymentRequest(
        BigDecimal paidAmount,
        String referenceNo,
        String slipUrl,
        LocalDateTime paidAt
) {
}
