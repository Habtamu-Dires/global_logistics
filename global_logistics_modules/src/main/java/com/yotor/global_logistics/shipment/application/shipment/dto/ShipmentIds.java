package com.yotor.global_logistics.shipment.application.shipment.dto;

import java.util.UUID;

public record ShipmentIds(
        Long id,
        UUID consignorId
) {
}
