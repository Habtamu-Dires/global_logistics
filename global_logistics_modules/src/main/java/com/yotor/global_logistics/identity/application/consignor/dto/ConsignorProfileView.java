package com.yotor.global_logistics.identity.application.consignor.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ConsignorProfileView(
        String phone,
        UUID publicId,
        String firstName,
        String lastName,
        String businessName,
        String tradeLicence,
        String status,
        String remark,
        Instant createdAt,
        Instant updatedAt
) {
}
