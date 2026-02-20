package com.yotor.global_logistics.driver_negotiation.api;

import com.yotor.global_logistics.driver_negotiation.domain.dto.NegotiationStatus;
import com.yotor.global_logistics.driver_negotiation.application.dto.DriverNegotiationResponse;

import java.util.UUID;

public interface DriverNegotiationQueryService {
    DriverNegotiationResponse markSelected(UUID negotiationId);
    void markDriversNotSelected(UUID shipmentId);
    void markOthersExpired(UUID shipmentId);
    int countByShipmentAndStatus(UUID shipmentId, NegotiationStatus status);


}
