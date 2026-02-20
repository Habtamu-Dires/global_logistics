package com.yotor.global_logistics.tracking.application.dto;

import com.yotor.global_logistics.tracking.entity.Tracking;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrackingResponse(
        Double latitude,
        Double longitude,
        Double accuracy,
        Double speed,
        LocalDateTime recordedAt
) {
    public static TrackingResponse from(Tracking tracking) {
        return TrackingResponse.builder()
                .latitude(tracking.getLatitude())
                .longitude(tracking.getLongitude())
                .accuracy(tracking.getAccuracy())
                .speed(tracking.getSpeed())
                .build();
    }
}
