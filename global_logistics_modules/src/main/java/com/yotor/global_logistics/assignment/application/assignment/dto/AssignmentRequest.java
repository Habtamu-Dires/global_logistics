package com.yotor.global_logistics.assignment.application.assignment.dto;

import java.util.UUID;

public record AssignmentRequest(
        UUID assignmentId,
        String remark
) {}
