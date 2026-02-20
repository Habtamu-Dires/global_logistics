package com.yotor.global_logistics.assignment.domain.assignment.dto;

import java.util.Map;

public enum AssignmentStatus {

    DRIVER_ASSIGNED,

    GDN_GENERATED,

    LOADING_CONFIRMED,

    IN_TRANSIT,

    DELIVERED,

    OFFLOADING_CONFIRMED,

    GRN_GENERATED,

    CONSIGNOR_CONFIRMED,

    CANCELLED,

    REOPENED_BY_ADMIN;

    public boolean isTerminal() {
        return this == CONSIGNOR_CONFIRMED
                || this == CANCELLED;
    }

    public boolean canTrack(){
        return this == GDN_GENERATED
                || this == IN_TRANSIT;
    }

    public static final Map<AssignmentStatus, AssignmentStatus> MILESTONE_FLOW = Map.of(
            AssignmentStatus.GDN_GENERATED, AssignmentStatus.DRIVER_ASSIGNED,
            AssignmentStatus.LOADING_CONFIRMED, AssignmentStatus.GDN_GENERATED,
            AssignmentStatus.IN_TRANSIT, AssignmentStatus.LOADING_CONFIRMED,
            AssignmentStatus.DELIVERED, AssignmentStatus.IN_TRANSIT,
            AssignmentStatus.OFFLOADING_CONFIRMED, AssignmentStatus.DELIVERED,
            AssignmentStatus.GRN_GENERATED, AssignmentStatus.OFFLOADING_CONFIRMED,
            AssignmentStatus.CONSIGNOR_CONFIRMED, AssignmentStatus.GRN_GENERATED
    );

}



