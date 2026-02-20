package com.yotor.global_logistics.identity.profile.dto;

import java.util.UUID;

public record DriverAdminView(
        String phone,
        UUID publicId,
        String firstName,
        String lastName,
        String profilePic,
        String nationalId,
        String licenceNumber,
        String licenceDocument,
        String region,
        String status
) {}
