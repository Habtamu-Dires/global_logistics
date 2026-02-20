package com.yotor.global_logistics.identity.vehicle.dto;

public record CreateVehicleRequest(
        String plateNumber,
        String insuranceDoc,
        String type,
        String details,
        String photo
) { }
