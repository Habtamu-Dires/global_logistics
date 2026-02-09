package com.yotor.global_logestics.identity.profile.dto;

import java.util.UUID;

public record DriverAdminView(
        UUID externalId,
        String firstName,
        String lastName,
        String profilePic,
        String nationalId,
        String licenceNumber,
        String licenceDocument,
        String region,
        String status
) {}
