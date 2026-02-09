package com.yotor.global_logestics.identity.profile.dto;

import lombok.Builder;


@Builder
public record CreateDriverProfileRequest(
        String profilePic,
        String nationalId,
        String licenceNumber,
        String licenceDocument,
        String region
) {}
