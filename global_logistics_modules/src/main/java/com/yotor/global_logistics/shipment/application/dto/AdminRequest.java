package com.yotor.global_logistics.shipment.application.dto;

import java.util.UUID;

public record AdminRequest(
        UUID shipmentId,
        String reason
) {
}
