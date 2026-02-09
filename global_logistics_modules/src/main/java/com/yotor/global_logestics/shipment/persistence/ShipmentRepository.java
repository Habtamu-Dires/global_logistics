package com.yotor.global_logestics.shipment.persistence;

import com.yotor.global_logestics.shipment.application.dto.ShipmentIds;
import com.yotor.global_logestics.shipment.application.dto.ShipmentOfferDto;
import com.yotor.global_logestics.shipment.application.dto.ShipmentStatusHistoryDto;
import com.yotor.global_logestics.shipment.domain.Shipment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends CrudRepository<Shipment,Long> {

    @Query("""
            SELECT * FROM shipment s
            WHERE s.external_id = :shipmentExternalId
            """)
    Optional<Shipment> findByExternalId(UUID shipmentExternalId);

    @Query("""
            SELECT s.id FROM shipment s
            WHERE s.external_id = :externalId
            """)
    Optional<Long> findIdByExternalId(UUID externalId);

    @Query("""
            SELECT s.id, s.consignor_id FROM shipment s
            WHERE s.external_id = :externalId
            """)
    Optional<ShipmentIds> findIdAndConsignorIdByExternalId(UUID externalId);

    @Query("""
            SELECT * FROM shipment s
            WHERE s.external_id = :externalId 
            AND s.consignor_id = :consignorId
            LIMIT 1
            """)
    Optional<Shipment> findByExternalIdAndConsignorId(UUID externalId, UUID consignorId);

    @Query("""
            SELECT * FROM shipment s
            WHERE s.consignor_id = :consignorId
            ORDER BY s.created_at
            """)
    List<Shipment> findByConsignorId(UUID consignorId);

    @Query("SELECT * FROM shipment")
    List<Shipment> findAllShipments();

    /** --- shipment status history queries ----- */
    @Query("""
            SELECT ssh.status, ssh.reason, ssh.changed_by, ssh.changed_at
            FROM shipment_status_history ssh
            WHERE ssh.shipment_id = :shipmentId
            ORDER BY ssh.changed_at
            """)
    List<ShipmentStatusHistoryDto> findStatusHistory(Long shipmentId);


    /** --- shipment offer queries --- */
    @Query("""
            SELECT so.round, so.price_amount, so.required_vehicle_type,
                so.required_vehicle_number, so.loading_date, so.delivery_date,
                so.reason, so.offered_by, so.offered_at 
            FROM shipment_offer so
            WHERE so.shipment_id = :shipmentId 
            ORDER By so.offered_at
            """)
    List<ShipmentOfferDto> getShipmentOffers(Long shipmentId);

}
