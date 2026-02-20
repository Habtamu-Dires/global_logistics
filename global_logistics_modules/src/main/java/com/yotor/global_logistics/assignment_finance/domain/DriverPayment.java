package com.yotor.global_logistics.assignment_finance.domain;

import com.yotor.global_logistics.assignment_finance.domain.enums.AssignmentFinanceStatus;
import com.yotor.global_logistics.assignment_finance.domain.enums.DriverPaymentStatus;
import com.yotor.global_logistics.shipment_finance.domain.enums.ShipmentPaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("driver_payment")
public class DriverPayment {

    @Id
    private Long id;

    private UUID publicId;

    private BigDecimal amount;

    private DriverPaymentStatus status;

    private String referenceNo;
    private String slipUrl;

    private LocalDateTime paidAt;

    private LocalDateTime verifiedAt;
    private UUID verifiedBy;

    private LocalDateTime voidedAt;
    private UUID voidedBy;
    private String voidReason;

    private LocalDateTime createdAt;

    // --- behavior ---
    public DriverPayment(){}

    @PersistenceCreator
    public DriverPayment(
            Long id,
            UUID publicId,
            BigDecimal amount,
            DriverPaymentStatus status,
            String referenceNo,
            String slipUrl,
            LocalDateTime paidAt,
            LocalDateTime verifiedAt,
            UUID verifiedBy,
            LocalDateTime voidedAt,
            UUID voidedBy,
            String voidReason,
            LocalDateTime createdAt
    ) {
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

    public static DriverPayment create(
            BigDecimal amount,
            String referenceNo,
            String slipUrl,
            LocalDateTime paidAt
    ){
        DriverPayment payment = new DriverPayment();
        payment.publicId = UUID.randomUUID();
        payment.amount = amount;
        payment.status = DriverPaymentStatus.PAID;
        payment.referenceNo = referenceNo;
        payment.slipUrl = slipUrl;
        payment.paidAt = paidAt;
        payment.createdAt = LocalDateTime.now();

        return payment;
    }


    public void verify(UUID adminId) {
        if (status != DriverPaymentStatus.PAID)
            throw new IllegalStateException("Already processed");

        status = DriverPaymentStatus.VERIFIED;
        verifiedAt = LocalDateTime.now();
        verifiedBy = adminId;
    }

    public void reject(UUID adminId) {
        if (status != DriverPaymentStatus.PAID)
            throw new IllegalStateException("Already processed");

        status = DriverPaymentStatus.REJECTED;
        verifiedAt = LocalDateTime.now();
        verifiedBy = adminId;
    }

    public void voidPayment(UUID adminId, String reason) {
        if (status == DriverPaymentStatus.VOIDED)
            throw new IllegalStateException("Already voided");

        status = DriverPaymentStatus.VOIDED;
        voidedAt = LocalDateTime.now();
        voidedBy = adminId;
        voidReason = reason;
    }

    public boolean isVerified() {
        return status == DriverPaymentStatus.VERIFIED;
    }

    public BigDecimal amount() {
        return amount;
    }

    public UUID publicId() {
        return publicId;
    }
}
