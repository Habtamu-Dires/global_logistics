package com.yotor.global_logistics.shipment.persistence;

import com.yotor.global_logistics.shipment.application.shipment.dto.ShipmentIds;
import com.yotor.global_logistics.shipment.application.offer.dto.ShipmentOfferDto;
import com.yotor.global_logistics.shipment.application.history.dto.ShipmentStatusHistoryDto;
import com.yotor.global_logistics.shipment.application.shipment.dto.ShipmentSummary;
import com.yotor.global_logistics.shipment.domain.Shipment;
import com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends CrudRepository<Shipment,Long> {

    @Query("""
            SELECT * FROM shipment s
            WHERE s.public_id = :shipmentPublicId
            """)
    Optional<Shipment> findByPublicId(UUID shipmentPublicId);


    @Query("""
            SELECT s.id, s.consignor_id FROM shipment s
            WHERE s.public_id = :externalId
            """)
    Optional<ShipmentIds> findIdAndConsignorIdByPublicId(UUID externalId);

    @Query("""
            SELECT * FROM shipment s
            WHERE s.public_id = :externalId 
            AND s.consignor_id = :consignorId
            LIMIT 1
            """)
    Optional<Shipment> findByPublicIdAndConsignorId(UUID externalId, UUID consignorId);

    @Query("""
            SELECT * FROM shipment s
            WHERE s.consignor_id = :consignorId
            ORDER BY s.created_at
            """)
    List<Shipment> findByConsignorId(UUID consignorId);

    @Query("""
            SELECT
                public_id,
                good_type,
                loading_location,
                offloading_location,
                price_amount,
                price_currency,
                required_vehicle_type,
                required_vehicle_number,
                loading_date,
                current_status,
                created_at
            FROM shipment 
            WHERE current_status IN (:statuses)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    List<ShipmentSummary> findPageOfShipmentsByStatus(List<ShipmentStatus> statuses, long limit, long offset);

    @Query("""
            SELECT COUNT(*) FROM shipment
            where current_status IN (:statuses)
            """)
    long countShipmentByStatus(List<ShipmentStatus> statuses);

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

    @Query("""
            SELECT s.required_vehicle_number FROM shipment s
            WHERE s.public_id = :shipmentId
            """)
    int findRequiredVehicleNumber(UUID shipmentId);

    @Query("""
            SELECT s.current_status FROM shipment s
            WHERE s.public_id = :shipmentId
            """)
    Optional<String> findShipmentStatus(UUID shipmentId);

    @Query("""
            SELECT s.consignor_id FROM shipment s
            WHERE s.public_id = :shipmentId
            """)
    Optional<UUID> findConsignorId(UUID shipmentId);
}
