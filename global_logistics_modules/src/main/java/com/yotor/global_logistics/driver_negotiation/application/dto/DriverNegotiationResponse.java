package com.yotor.global_logistics.driver_negotiation.application.dto;

import com.yotor.global_logistics.driver_negotiation.domain.DriverNegotiation;
import com.yotor.global_logistics.driver_negotiation.domain.dto.NegotiationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DriverNegotiationResponse(
         UUID publicId,

         UUID shipmentId,
         UUID driverId,

         NegotiationStatus status,
         BigDecimal finalAgreedPrice,

         String startLocation,
         Double startLatitude,
         Double startLongitude,

         LocalDateTime createdAt,
         LocalDateTime updatedAt
) {
    public  static DriverNegotiationResponse from(DriverNegotiation negotiation) {
        return DriverNegotiationResponse.builder()
                .publicId(negotiation.getPublicId())
                .shipmentId(negotiation.getShipmentId())
                .driverId(negotiation.getDriverId())
                .status(negotiation.getStatus())
                .finalAgreedPrice(negotiation.getFinalAgreedPrice())
                .startLocation(negotiation.getStartLocation())
                .startLatitude(negotiation.getStartLatitude())
                .startLongitude(negotiation.getStartLongitude())
                .createdAt(negotiation.getCreatedAt())
                .updatedAt(negotiation.getUpdatedAt())
                .build();
    }
}
