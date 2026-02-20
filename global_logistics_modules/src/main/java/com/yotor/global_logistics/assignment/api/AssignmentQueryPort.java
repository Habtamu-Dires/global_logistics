package com.yotor.global_logistics.assignment.api;

import com.yotor.global_logistics.assignment.api.dto.AssignmentTrackingRes;

import java.util.UUID;

public interface AssignmentQueryPort {
    AssignmentTrackingRes findByExternalIdForTracking(UUID assignmentId);
    UUID getShipmentId(UUID assignmentId);
    boolean isAssignmentCompleted(UUID assignmentPublicId);
    UUID getDriverId(UUID assignmentPublicId);

}
