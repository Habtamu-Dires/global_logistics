package com.yotor.global_logistics.identity.application.vehicle.dto;

import java.util.UUID;

public record VehicleProfileView(
        String phone,
        UUID publicId,
        String type,
        String plateNumber,
        String details,
        String insuranceDoc,
        String status,
        String photo
) {
}
