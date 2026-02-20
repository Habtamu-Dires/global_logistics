package com.yotor.global_logistics.identity.domain.vehicle;

import java.util.Arrays;

public enum VehicleStatus {
    PENDING,
    APPROVED,
    INACTIVE,
    REJECTED;

    public static VehicleStatus from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown VehicleStatus: " + value));
    }
}
