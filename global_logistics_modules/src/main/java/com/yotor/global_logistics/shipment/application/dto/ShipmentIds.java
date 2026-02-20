package com.yotor.global_logistics.shipment.application.dto;

import java.util.UUID;

public record ShipmentIds(
        Long id,
        UUID consignorId
) {
}
