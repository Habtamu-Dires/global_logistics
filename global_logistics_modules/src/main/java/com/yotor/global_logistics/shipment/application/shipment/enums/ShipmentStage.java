package com.yotor.global_logistics.shipment.application.shipment.enums;

import com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus;

import java.util.List;

import static com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus.*;
import static com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus.ADMIN_REJECTED_OFFER;
import static com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus.CONSIGNOR_REJECTED_OFFER;

public enum ShipmentStage {
    NEW(List.of(CREATED,ADMIN_REQUESTED_CHANGE,CONSIGNOR_COUNTERED,ADMIN_REJECTED_OFFER,CONSIGNOR_REJECTED_OFFER)),
    AGREED(List.of(CONSIGNOR_ACCEPTED, ADMIN_APPROVED)),
    EXECUTION(List.of(DRIVER_ASSIGNED,IN_TRANSIT)),
    COMPLETED(List.of(ShipmentStatus.COMPLETED)),
    CANCELLED(List.of(CANCELLED_SYSTEM,CANCELLED_BY_ADMIN,CANCELLED_BY_CONSIGNOR));

    private final List<ShipmentStatus> statuses;

    ShipmentStage(List<ShipmentStatus> statuses) {
        this.statuses = statuses;
    }

    public List<ShipmentStatus> statuses() {
        return statuses;
    }
}
