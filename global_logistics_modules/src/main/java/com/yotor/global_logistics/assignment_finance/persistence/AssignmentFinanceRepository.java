package com.yotor.global_logistics.assignment_finance.persistence;

import com.yotor.global_logistics.assignment_finance.domain.AssignmentFinance;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentFinanceRepository extends CrudRepository<AssignmentFinance,Long> {

    @Query("""
            SELECT * FROM assignment_finance af
            JOIN driver_payment dp
            ON dp.assignment_finance.id = af.id
            WHERE dp.public_id = :paymentPublicId
            AND dp.status != 'VOIDED'
            """)
    Optional<AssignmentFinance> findByPaymentPublicId(UUID paymentPublicId);

    @Query("""
            SELECT * FROM assignment_finance 
            WHERE public_id = :assignmentFinanceId
            """)
    Optional<AssignmentFinance> findByPublicId(UUID assignmentFinanceId);
}
