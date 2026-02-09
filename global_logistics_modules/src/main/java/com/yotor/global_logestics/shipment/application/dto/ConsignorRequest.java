package com.yotor.global_logestics.shipment.application.dto;

import java.util.UUID;

public record ConsignorRequest(
        UUID shipmentId,
        String reason
) {
}
