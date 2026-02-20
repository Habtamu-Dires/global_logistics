package com.yotor.global_logistics.assignment_finance.application;

import com.yotor.global_logistics.assignment_finance.domain.AssignmentFinance;
import com.yotor.global_logistics.assignment_finance.persistence.AssignmentFinanceRepository;
import com.yotor.global_logistics.assignment_finance.api.AssignmentFinancePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentFinancePortImp implements AssignmentFinancePort {

    private final AssignmentFinanceRepository financeRepo;

    @Override
    public void createFinanceForAssignment(UUID assignmentPublicId, BigDecimal driverAgreedAmount) {
        AssignmentFinance finance = AssignmentFinance
                .create(assignmentPublicId, driverAgreedAmount);

        financeRepo.save(finance);
    }
}
