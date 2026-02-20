package com.yotor.global_logistics.identity.api;

import com.yotor.global_logistics.identity.application.dto.UserSummary;
import com.yotor.global_logistics.identity.application.dto.VehicleSummery;

import java.util.UUID;

public interface IdentityQueryService {

    UserSummary getUserSummary(UUID userId);
    VehicleSummery getVehicleSummery(UUID driverId);

    boolean isDriverApproved(UUID driverExternalId);
    boolean isConsignorApproved(UUID consignorExternalId);
}
