package com.yotor.global_logestics.shipment.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("shipment_offer")
public class ShipmentOffer {

    @Id
    private Long id;

    private int round;

    private BigDecimal priceAmount;

    private String requiredVehicleType;
    private int requiredVehicleNumber;

    private LocalDateTime loadingDate;
    private LocalDateTime deliveryDate;

    private String reason;
    private UUID offeredBy;
    private LocalDateTime offeredAt;

    public ShipmentOffer(
            int round,
            BigDecimal priceAmount,
            String requiredVehicleType,
            int requiredVehicleNumber,
            LocalDateTime loadingDate,
            LocalDateTime deliveryDate,
            String reason,
            UUID offeredBy
    ) {
        this.round = round;
        this.priceAmount = priceAmount;
        this.requiredVehicleType = requiredVehicleType;
        this.requiredVehicleNumber = requiredVehicleNumber;
        this.loadingDate = loadingDate;
        this.deliveryDate = deliveryDate;
        this.reason = reason;
        this.offeredBy = offeredBy;
        this.offeredAt = LocalDateTime.now();
    }

    // getters
}


