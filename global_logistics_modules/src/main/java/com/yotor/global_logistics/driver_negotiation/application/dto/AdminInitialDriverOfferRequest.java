package com.yotor.global_logistics.driver_negotiation.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AdminInitialDriverOfferRequest(
    UUID shipmentId,
    List<UUID> driverIds,
    BigDecimal offeredPrice,
    String remark
) {}
