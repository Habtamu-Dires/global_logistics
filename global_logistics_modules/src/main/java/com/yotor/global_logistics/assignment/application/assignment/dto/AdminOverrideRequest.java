package com.yotor.global_logistics.assignment.application.assignment.dto;

import com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus;

import java.util.UUID;

public record AdminOverrideRequest(
        UUID assignmentId,
        AssignmentStatus targetStatus,
        String reason
) {
}
