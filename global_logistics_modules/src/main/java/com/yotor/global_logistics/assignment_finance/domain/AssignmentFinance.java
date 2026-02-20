package com.yotor.global_logistics.assignment_finance.domain;

import com.yotor.global_logistics.assignment_finance.domain.enums.AssignmentFinanceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("assignment_finance")
public class AssignmentFinance {
    @Id
    private Long id;

    private UUID publicId;

    private UUID assignmentId;

    private BigDecimal agreedAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;

    private AssignmentFinanceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @MappedCollection(idColumn = "assignment_finance_id")
    private Set<DriverPayment> payments = new HashSet<>();

    // --- behavior ---
    public AssignmentFinance(){}

    @PersistenceCreator
    public AssignmentFinance(
            Long id,
            UUID publicId,
            UUID assignmentId,
            BigDecimal agreedAmount,
            BigDecimal paidAmount,
            BigDecimal remainingAmount,
            AssignmentFinanceStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){
        this.id = id;
        this.publicId = publicId;
        this.assignmentId = assignmentId;
        this.agreedAmount = agreedAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // methods
    public static AssignmentFinance create(UUID assignmentId, BigDecimal agreedAmount) {
        AssignmentFinance finance = new AssignmentFinance();

        finance.assignmentId = assignmentId;
        finance.agreedAmount = agreedAmount;
        finance.paidAmount = BigDecimal.ZERO;
        finance.remainingAmount = agreedAmount;
        finance.status = AssignmentFinanceStatus.UNPAID;
        finance.createdAt = LocalDateTime.now();
        finance.updatedAt = LocalDateTime.now();
        return finance;
    }


    public void registerPayment(DriverPayment payment) {
        payments.add(payment);
    }

    public void verifyPayment(UUID paymentPublicId, UUID adminId) {

        DriverPayment payment = getPayment(paymentPublicId);

        payment.verify(adminId);

        applyVerifiedAmount(payment.amount());
    }

    public void voidPayment(UUID paymentPublicId, UUID adminId, String reason) {

        DriverPayment payment = getPayment(paymentPublicId);

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
            status = AssignmentFinanceStatus.UNPAID;
        }
        else if (remainingAmount.equals(BigDecimal.ZERO)) {
            status = AssignmentFinanceStatus.PAID;
        }
        else {
            status = AssignmentFinanceStatus.PARTIALLY_PAID;
        }
    }

    private DriverPayment getPayment(UUID publicId) {
        return payments.stream()
                .filter(p -> p.publicId().equals(publicId))
                .findFirst()
                .orElseThrow();
    }
}
