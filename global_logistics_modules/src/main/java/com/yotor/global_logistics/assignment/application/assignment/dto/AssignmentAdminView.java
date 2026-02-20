package com.yotor.global_logistics.assignment.application.assignment.dto;

import com.yotor.global_logistics.assignment.domain.assignment.ShipmentAssignment;
import com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AssignmentAdminView(
         UUID publicId,

         UUID shipmentId,
         UUID driverId,

         BigDecimal agreedPrice,

         String startLocation,
         Double startLatitude,
         Double startLongitude,

         AssignmentStatus status,

         UUID assignedBy,
         LocalDateTime assignedAt,

         LocalDateTime driverStartedAt,
         LocalDateTime startedAt,

         LocalDateTime driverCompletedAt,
         LocalDateTime completedAt,

         LocalDateTime consignorConfirmedAt,

         LocalDateTime cancelledAt,

         String cancelReason,
         String remark

) {

    public static AssignmentAdminView from(ShipmentAssignment assignment) {

        return AssignmentAdminView.builder()
                .publicId(assignment.getPublicId())
                .shipmentId(assignment.getShipmentId())
                .driverId(assignment.getDriverId())
                .agreedPrice(assignment.getAgreedPrice())
                .startLocation(assignment.getStartLocation())
                .startLatitude(assignment.getStartLatitude())
                .startLongitude(assignment.getStartLongitude())
                .status(assignment.getStatus())
                .assignedBy(assignment.getAssignedBy())
                .assignedAt(assignment.getAssignedAt())
                .startedAt(assignment.getStartedAt())
                .consignorConfirmedAt(assignment.getConsignorConfirmedAt())
                .cancelledAt(assignment.getCancelledAt())
                .cancelReason(assignment.getCancelReason())
                .build();
    }
}
