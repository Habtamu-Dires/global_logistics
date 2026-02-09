package com.yotor.global_logestics.identity.profile.dto;

public record CreateConsignorProfileRequest(
        String businessName,
        String tradeLicence
) {
}
