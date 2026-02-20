package com.yotor.global_logistics.tracking.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TrackingRecordRequest(
        UUID assignmentId,
        Double latitude,
        Double longitude,
        Double accuracy,
        Double speed,
        LocalDateTime recordedAt
) {}
