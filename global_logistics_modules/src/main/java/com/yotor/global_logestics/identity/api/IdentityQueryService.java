package com.yotor.global_logestics.identity.api;

import com.yotor.global_logestics.identity.api.dto.UserSummary;

import java.util.Optional;
import java.util.UUID;

public interface IdentityQueryService {
    Optional<UserSummary> findUserByExternalId(UUID userExternalId);

    boolean isUserApproved(UUID userExternalId);

    boolean isVehicleApproved(UUID vehicleExternalId);
}
