package com.yotor.global_logistics.identity.port;

import com.yotor.global_logistics.identity.application.identity.dto.UserProfile;
import com.yotor.global_logistics.identity.application.vehicle.dto.VehicleSummery;

import java.util.UUID;

public interface IdentityQueryPort {

    UserProfile getUserSummary(UUID userId);
    VehicleSummery getVehicleSummery(UUID driverId);
    boolean isDriverApproved(UUID driverExternalId);
    boolean isConsignorApproved(UUID consignorExternalId);
}
