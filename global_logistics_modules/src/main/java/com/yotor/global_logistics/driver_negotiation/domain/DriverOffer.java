package com.yotor.global_logistics.driver_negotiation.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("driver_offer")
public class DriverOffer {

    @Id
    private Long id;

    private final int round;

    private final BigDecimal priceAmount;
    private final UUID offeredBy;
    private final LocalDateTime offeredAt;

    private final String reason;

    @PersistenceCreator
    public DriverOffer(
            Long id,
            int round,
            BigDecimal priceAmount,
            UUID offeredBy,
            LocalDateTime offeredAt,
            String reason
    ){
        this.id = id;
        this.round = round;
        this.priceAmount = priceAmount;
        this.offeredBy = offeredBy;
        this.offeredAt = offeredAt;
        this.reason = reason;
    }

    public DriverOffer(
                       int round,
                       BigDecimal priceAmount,
                       UUID offeredBy,
                       String reason
    ) {
        this.round = round;
        this.priceAmount = priceAmount;
        this.offeredBy = offeredBy;
        this.offeredAt = LocalDateTime.now();
        this.reason = reason;
    }

}
