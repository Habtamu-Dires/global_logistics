package com.yotor.global_logistics.identity.application.driver.dto;

import lombok.Builder;


@Builder
public record CreateDriverProfileRequest(
        String profilePic,
        String nationalId,
        String licenceNumber,
        String licenceDocument,
        String preferredLanes
) {}
