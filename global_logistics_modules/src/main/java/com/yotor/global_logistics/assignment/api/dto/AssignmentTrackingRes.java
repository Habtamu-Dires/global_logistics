package com.yotor.global_logistics.assignment.api.dto;

import com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus;

import java.util.UUID;


public record AssignmentTrackingRes(
        UUID driverId,
        AssignmentStatus status
) {
}
