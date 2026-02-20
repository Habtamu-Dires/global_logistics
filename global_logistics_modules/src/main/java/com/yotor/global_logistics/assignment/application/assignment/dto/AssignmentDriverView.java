package com.yotor.global_logistics.assignment.application.assignment.dto;

import com.yotor.global_logistics.assignment.domain.assignment.ShipmentAssignment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AssignmentDriverView(
        UUID publicId,
        UUID shipmentId,
        String status,
        BigDecimal agreedPrice,
        LocalDateTime assignedAt,

        LocalDateTime driverStartedAt,
        LocalDateTime startedAt,

        LocalDateTime driverCompletedAt,
        LocalDateTime completedAt,

        LocalDateTime consignorConfirmedAt,

        LocalDateTime cancelledAt,

        String cancelReason
)  {
    public static AssignmentDriverView from(ShipmentAssignment shipmentAssignment) {
        return AssignmentDriverView.builder()
                .publicId(shipmentAssignment.getPublicId())
                .shipmentId(shipmentAssignment.getShipmentId())
                .status(shipmentAssignment.getStatus().name())
                .agreedPrice(shipmentAssignment.getAgreedPrice())
                .assignedAt(shipmentAssignment.getAssignedAt())
                .startedAt(shipmentAssignment.getStartedAt())
                .consignorConfirmedAt(shipmentAssignment.getConsignorConfirmedAt())
                .cancelledAt(shipmentAssignment.getCancelledAt())
                .cancelReason(shipmentAssignment.getCancelReason())
                .build();
    }
}
