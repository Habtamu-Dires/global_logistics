package com.yotor.global_logistics.shipment_finance.domain;

import com.yotor.global_logistics.shipment_finance.domain.enums.ShipmentFinanceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("shipment_finance")
public class ShipmentFinance {

    @Id
    private Long id;

    private UUID publicId;

    private UUID shipmentId;

    private BigDecimal agreedAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;

    private ShipmentFinanceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @MappedCollection(idColumn = "shipment_finance_id")
    private Set<ShipmentPayment> payments = new HashSet<>();

    // constructors
    private ShipmentFinance(){}
    @PersistenceCreator
    public ShipmentFinance(
            Long id,
            UUID publicId,
            UUID shipmentId,
            BigDecimal agreedAmount,
            BigDecimal paidAmount,
            BigDecimal remainingAmount,
            ShipmentFinanceStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){
        this.id = id;
        this.publicId = publicId;
        this.shipmentId = shipmentId;
        this.agreedAmount = agreedAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- behavior ---
    public static ShipmentFinance create(UUID shipmentId, BigDecimal agreedAmount) {
        ShipmentFinance finance = new ShipmentFinance();
        finance.publicId = UUID.randomUUID();
        finance.shipmentId = shipmentId;
        finance.agreedAmount = agreedAmount;
        finance.paidAmount = BigDecimal.ZERO;
        finance.remainingAmount = agreedAmount;
        finance.status = ShipmentFinanceStatus.UNPAID;
        finance.createdAt = LocalDateTime.now();
        finance.updatedAt = LocalDateTime.now();
        return finance;
    }

    public void registerPayment(ShipmentPayment payment) {
        payments.add(payment);
    }

    public void verifyPayment(UUID paymentPublicId, UUID adminId) {

        ShipmentPayment payment = getPayment(paymentPublicId);

        payment.verify(adminId);

        applyVerifiedAmount(payment.amount());
    }

    public void voidPayment(UUID paymentPublicId, UUID adminId, String reason) {

        ShipmentPayment payment = getPayment(paymentPublicId);

        boolean wasVerified = payment.isVerified();

        payment.voidPayment(adminId, reason);

        if (wasVerified) {
            rollbackAmount(payment.amount());
        }
    }

    private void applyVerifiedAmount(BigDecimal amount) {
        paidAmount = paidAmount.add(amount);
        remainingAmount = agreedAmount.subtract(paidAmount);
        recalculateStatus();
    }

    private void rollbackAmount(BigDecimal amount) {
        paidAmount = paidAmount.subtract(amount);
        remainingAmount = agreedAmount.subtract(paidAmount);
        recalculateStatus();
    }

    private void recalculateStatus() {
        if (paidAmount.equals(BigDecimal.ZERO)) {
            status = ShipmentFinanceStatus.UNPAID;
        }
        else if (remainingAmount.equals(BigDecimal.ZERO)) {
            status = ShipmentFinanceStatus.PAID;
        } else if(remainingAmount.signum() < 0){
            status = ShipmentFinanceStatus.OVER_PAID;
        }
        else {
            status = ShipmentFinanceStatus.PARTIALLY_PAID;
        }
    }

    private ShipmentPayment getPayment(UUID publicId) {
        return payments.stream()
                .filter(p -> p.publicId().equals(publicId))
                .findFirst()
                .orElseThrow();
    }
}


