package com.yotor.global_logestics.identity.vehicle.dto;

import java.util.UUID;

public record VehicleProfileView(
        String phone,
        UUID externalId,
        String type,
        String plateNumber,
        String details,
        String insuranceDoc,
        String status,
        String photo
) {
}
