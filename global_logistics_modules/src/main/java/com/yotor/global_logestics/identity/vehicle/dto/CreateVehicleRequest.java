package com.yotor.global_logestics.identity.vehicle.dto;

public record CreateVehicleRequest(
        String plateNumber,
        String insuranceDoc,
        String type,
        String details,
        String photo
) { }
