package com.yotor.global_logistics.shipment.application.dto;

import com.yotor.global_logistics.shipment.domain.Shipment;
import com.yotor.global_logistics.shipment.domain.dto.ShipmentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ShipmentResponse(
        ShipmentStatus currentStatus,
        UUID consignorId,
        UUID publicId,
        BigDecimal priceAmount,
        String priceType,
        String goodType,
        String weight,
        String volume,
        String loadingLocation,
        String offloadingLocation,
        String route,
        String requiredVehicleType,
        int requiredVehicleNumber,
        LocalDateTime loadingDate,
        LocalDateTime deliveryDate,
        String details
) {

    public static ShipmentResponse from(Shipment s){
        return ShipmentResponse.builder()
                .currentStatus(s.getCurrentStatus())
                .consignorId(s.getConsignorId())
                .publicId(s.getPublicId())
                .priceAmount(s.getPriceAmount())
                .priceType(s.getPriceType())
                .goodType(s.getGoodType())
                .weight(s.getWeight())
                .volume(s.getVolume())
                .loadingLocation(s.getLoadingLocation())
                .offloadingLocation(s.getOffloadingLocation())
                .route(s.getRoute())
                .requiredVehicleType(s.getRequiredVehicleType())
                .requiredVehicleNumber(s.getRequiredVehicleNumber())
                .loadingDate(s.getLoadingDate())
                .deliveryDate(s.getDeliveryDate())
                .details(s.getDetails())
                .build();
    }
}
