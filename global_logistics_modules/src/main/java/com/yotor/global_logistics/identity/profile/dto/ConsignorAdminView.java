package com.yotor.global_logistics.identity.profile.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ConsignorAdminView(
        String phone,
        UUID publicId,
        String firstName,
        String lastName,
        String businessName,
        String tradeLicence,
        String status
) {
}
