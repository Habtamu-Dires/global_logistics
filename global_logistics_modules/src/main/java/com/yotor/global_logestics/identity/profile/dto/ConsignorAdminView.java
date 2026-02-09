package com.yotor.global_logestics.identity.profile.dto;

import java.util.UUID;

public record ConsignorAdminView(
        UUID externalId,
        String firstName,
        String lastName,
        String businessName,
        String tradeLicence,
        String status
) {
}
