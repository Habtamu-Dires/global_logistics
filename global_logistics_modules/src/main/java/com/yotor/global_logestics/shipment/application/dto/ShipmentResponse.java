package com.yotor.global_logestics.shipment.application.dto;

import com.yotor.global_logestics.shipment.domain.Shipment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ShipmentResponse(
        UUID consignorId,
        UUID externalId,
        BigDecimal priceAmount,
        String goodType,
        String weight,
        String volume,
        String loadingLocation,
        String offloadingLocation,
        String requiredVehicleType,
        int requiredVehicleNumber,
        LocalDateTime loadingDate,
        LocalDateTime deliveryDate,
        String details
) {

    public static ShipmentResponse from(Shipment s){
        return ShipmentResponse.builder()
                .consignorId(s.getConsignorId())
                .externalId(s.getExternalId())
                .priceAmount(s.getPriceAmount())
                .goodType(s.getGoodType())
                .weight(s.getWeight())
                .volume(s.getVolume())
                .loadingLocation(s.getLoadingLocation())
                .offloadingLocation(s.getOffloadingLocation())
                .requiredVehicleType(s.getRequiredVehicleType())
                .requiredVehicleNumber(s.getRequiredVehicleNumber())
                .loadingDate(s.getLoadingDate())
                .deliveryDate(s.getDeliveryDate())
                .details(s.getDetails())
                .build();
    }
}
