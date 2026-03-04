package com.yotor.global_logistics.shipment.port;

import com.yotor.global_logistics.shipment.application.shipment.dto.ShipmentResponse;

import java.util.UUID;

public interface ShipmentQueryPort {

    int getRequiredVehicleNumber(UUID shipmentId);
    boolean isShipmentOpenToDriverAssignment(UUID shipmentId);
    void markDriverAssigned(UUID shipmentId, UUID actorId);
    void markInTransit(UUID shipmentId, UUID actorId);
    void markCompleted(UUID shipmentId, UUID actorId);
    UUID getConsignorId(UUID shipmentId);
    ShipmentResponse getShipmentDetails(UUID shipmentId);


}
