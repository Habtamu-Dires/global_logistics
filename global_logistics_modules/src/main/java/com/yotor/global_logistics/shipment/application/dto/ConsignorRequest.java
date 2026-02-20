package com.yotor.global_logistics.shipment.application.dto;

import java.util.UUID;

public record ConsignorRequest(
        UUID shipmentId,
        String reason
) {
}
