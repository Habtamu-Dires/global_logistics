package com.yotor.global_logistics.driver_negotiation.application.dto;

import com.yotor.global_logistics.driver_negotiation.domain.DriverNegotiation;
import com.yotor.global_logistics.shipment.application.dto.ShipmentResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record DriverOfferView(
        UUID negotiationId,
        String loadingLocation,
        String offloadingLocation,
        String goodType,
        String weight,
        String volume,
        String requiredVehicleType,
        int requiredVehicleNumber,
        BigDecimal priceAmount,
        String priceType,
        String priceCurrency,
        String details,
        String status
) {

    public static DriverOfferView from(ShipmentResponse shipment, DriverNegotiation negotiation){
        return DriverOfferView.builder()
                .negotiationId(negotiation.getPublicId())
                .loadingLocation(shipment.loadingLocation())
                .offloadingLocation(shipment.offloadingLocation())
                .goodType(shipment.goodType())
                .weight(shipment.weight())
                .volume(shipment.volume())
                .requiredVehicleType(shipment.requiredVehicleType())
                .requiredVehicleNumber(shipment.requiredVehicleNumber())
                .priceAmount(negotiation.getFinalAgreedPrice())
                .priceType(shipment.goodType())
                .details(shipment.details())
                .status(negotiation.getStatus().name())
                .build();
    }
}
