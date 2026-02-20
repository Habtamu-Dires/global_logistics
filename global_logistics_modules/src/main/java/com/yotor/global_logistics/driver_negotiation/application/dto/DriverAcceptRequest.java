package com.yotor.global_logistics.driver_negotiation.application.dto;

import java.util.UUID;

public record DriverAcceptRequest(
        UUID negotiationId,
        Double lat,
        Double lon,
        String locationText
) {
}
