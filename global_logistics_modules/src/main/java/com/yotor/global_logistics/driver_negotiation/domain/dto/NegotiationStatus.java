package com.yotor.global_logistics.driver_negotiation.domain.dto;

import java.util.List;

public enum NegotiationStatus {

    CREATED,
    OFFER_SENT,          // Admin sent first offer
    DRIVER_COUNTERED,
    ADMIN_COUNTERED,
    DRIVER_ACCEPTS,
    DRIVER_REJECTS,
    EXPIRED,
    SELECTED,            // Admin selected this driver
    NOT_SELECTED,
    DRIVER_CANCEL,
    CANCELED;

    public static List<NegotiationStatus> getActiveStatuses() {
        return List.of(OFFER_SENT, DRIVER_COUNTERED, ADMIN_COUNTERED, DRIVER_ACCEPTS, SELECTED);
    }
}
