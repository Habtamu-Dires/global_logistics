package com.yotor.global_logistics.identity.application.driver.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DriverProfileView(
        String phone,
        UUID publicId,
        String firstName,
        String lastName,
        String profilePic,
        String nationalId,
        String licenceNumber,
        String licenceDocument,
        String preferredLanes,
        String status,
        String remark,
        Instant createdAt,
        Instant updatedAt
) {
}
