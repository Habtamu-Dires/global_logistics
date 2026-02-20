package com.yotor.global_logistics.assignment.event;


import java.util.UUID;

public record AssignmentCreatedEvent(
        UUID assignmentPublicId,
        UUID driverId,
        UUID shipmentId
) {}

