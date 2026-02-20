package com.yotor.global_logistics.identity.profile.dto;

public record CreateConsignorProfileRequest(
        String businessName,
        String tradeLicence
) {
}
