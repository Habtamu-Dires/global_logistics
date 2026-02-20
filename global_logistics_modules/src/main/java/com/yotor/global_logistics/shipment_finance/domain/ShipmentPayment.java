package com.yotor.global_logistics.shipment_finance.domain;

import com.yotor.global_logistics.shipment_finance.domain.enums.ShipmentPaymentStatus;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("shipment_payment")
public class ShipmentPayment {

    @Id
    private Long id;

    private UUID publicId;

    private BigDecimal amount;

    private ShipmentPaymentStatus status;

    private String referenceNo;
    private String slipUrl;

    private LocalDateTime paidAt;

    private LocalDateTime verifiedAt;
    private UUID verifiedBy;

    private LocalDateTime voidedAt;
    private UUID voidedBy;
    private String voidReason;

    private LocalDateTime createdAt;

    // constructors
    public ShipmentPayment(){}

    @PersistenceCreator
    public ShipmentPayment(
            Long id,
            UUID publicId,
            BigDecimal amount,
            ShipmentPaymentStatus status,
            String referenceNo,
            String slipUrl,
            LocalDateTime paidAt,
            LocalDateTime verifiedAt,
            UUID verifiedBy,
            LocalDateTime voidedAt,
            UUID voidedBy,
            String voidReason,
            LocalDateTime createdAt
    ){
        this.id = id;
        this.publicId = publicId;
        this.amount = amount;
        this.status = status;
        this.referenceNo = referenceNo;
        this.slipUrl = slipUrl;
        this.paidAt = paidAt;
        this.verifiedAt = verifiedAt;
        this.verifiedBy = verifiedBy;
        this.voidedAt = voidedAt;
        this.voidedBy = voidedBy;
        this.voidReason = voidReason;
        this.createdAt = createdAt;
    }

    // methods
    public static ShipmentPayment create(
            BigDecimal amount,
            String referenceNo,
            String slipUrl,
            LocalDateTime paidAt
    ){
        ShipmentPayment payment = new ShipmentPayment();
        payment.publicId = UUID.randomUUID();
        payment.amount = amount;
        payment.referenceNo = referenceNo;
        payment.slipUrl = slipUrl;
        payment.paidAt = paidAt;
        payment.status = ShipmentPaymentStatus.PAID;
        payment.createdAt = LocalDateTime.now();

        return payment;
    }

    public void verify(UUID adminId) {
        if (status != ShipmentPaymentStatus.PAID)
            throw new IllegalStateException("Already processed");

        status = ShipmentPaymentStatus.VERIFIED;
        verifiedAt = LocalDateTime.now();
        verifiedBy = adminId;
    }

    public void reject(UUID adminId) {
        if (status != ShipmentPaymentStatus.PAID)
            throw new IllegalStateException("Already processed");

        status = ShipmentPaymentStatus.REJECTED;
        verifiedAt = LocalDateTime.now();
        verifiedBy = adminId;
    }

    public void voidPayment(UUID adminId, String reason) {
        if (status == ShipmentPaymentStatus.VOIDED)
            throw new IllegalStateException("Already voided");

        status = ShipmentPaymentStatus.VOIDED;
        voidedAt = LocalDateTime.now();
        voidedBy = adminId;
        voidReason = reason;
    }

    public boolean isVerified() {
        return status == ShipmentPaymentStatus.VERIFIED;
    }

    public BigDecimal amount() {
        return amount;
    }

    public UUID publicId() {
        return publicId;
    }
}

