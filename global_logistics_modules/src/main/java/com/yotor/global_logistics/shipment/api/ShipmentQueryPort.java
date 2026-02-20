package com.yotor.global_logistics.shipment.api;

import com.yotor.global_logistics.shipment.application.dto.ShipmentResponse;

import java.util.UUID;

public interface ShipmentQueryPort {

    int getRequiredVehicleNumber(UUID shipmentId);
    boolean isShipmentOpenToDriverAssignment(UUID shipmentId);
    void markDriverAssigned(UUID shipmentId, UUID actorId);
    void markInProgress(UUID shipmentId, UUID actorId);
    void markCompleted(UUID shipmentId, UUID actorId);
    UUID getConsignorId(UUID shipmentId);
    ShipmentResponse getShipmentDetails(UUID shipmentId);


}
