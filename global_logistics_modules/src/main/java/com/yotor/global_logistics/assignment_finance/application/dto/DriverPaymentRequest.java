package com.yotor.global_logistics.assignment_finance.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DriverPaymentRequest(
        BigDecimal paidAmount,
        String referenceNo,
        String slipUrl,
        LocalDateTime paidAt
) {}
