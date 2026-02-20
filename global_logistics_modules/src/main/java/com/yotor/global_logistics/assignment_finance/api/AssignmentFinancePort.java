package com.yotor.global_logistics.assignment_finance.api;

import java.math.BigDecimal;
import java.util.UUID;

public interface AssignmentFinancePort {

    void createFinanceForAssignment(
            UUID assignmentPublicId,
            BigDecimal driverAgreedAmount
    );
}
