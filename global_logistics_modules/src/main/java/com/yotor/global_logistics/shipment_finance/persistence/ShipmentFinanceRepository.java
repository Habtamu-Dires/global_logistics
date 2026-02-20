package com.yotor.global_logistics.shipment_finance.persistence;

import com.yotor.global_logistics.shipment_finance.domain.ShipmentFinance;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentFinanceRepository extends CrudRepository<ShipmentFinance,Long> {

    @Query("""
            SELECT * FROM shipment_finance 
            WHERE public_id = :shipmentFinanceId
            """)
    Optional<ShipmentFinance> findByPublicId(UUID shipmentFinanceId);
    @Query("""
            SELECT * FROM shipment_finance sf
            JOIN shipment_payment sp
            ON sp.shipment_finance.id = sf.id
            WHERE sp.public_id = :paymentPublicId
            AND sp.status != 'VOIDED'
            """)
    Optional<ShipmentFinance> findByPaymentPublicId(UUID paymentPublicId);
}
