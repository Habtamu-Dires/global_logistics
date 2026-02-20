package com.yotor.global_logistics.assignment_finance.application;

import com.yotor.global_logistics.assignment_finance.application.dto.DriverPaymentRequest;
import com.yotor.global_logistics.assignment_finance.domain.AssignmentFinance;
import com.yotor.global_logistics.assignment_finance.domain.DriverPayment;
import com.yotor.global_logistics.assignment_finance.persistence.AssignmentFinanceRepository;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentFinanceService {

    private final AssignmentFinanceRepository financeRepo;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createDriverPayment(UUID assignmentFinanceId, DriverPaymentRequest req){
        AssignmentFinance finance = financeRepo.findByPublicId(assignmentFinanceId)
                .orElseThrow(()-> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        DriverPayment payment = DriverPayment.create(
                req.paidAmount(),
                req.referenceNo(),
                req.slipUrl(),
                req.paidAt()
        );

        finance.registerPayment(payment);

        financeRepo.save(finance);
    }

    @Transactional
    public void verifyDriverPayment(UUID paymentPublicId, UUID adminId) {

        AssignmentFinance finance =
                financeRepo.findByPaymentPublicId(paymentPublicId)
                        .orElseThrow(()-> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        finance.verifyPayment(paymentPublicId, adminId);

        financeRepo.save(finance);
    }

}
