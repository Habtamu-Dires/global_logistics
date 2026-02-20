package com.yotor.global_logistics.driver_negotiation.persistence;

import com.yotor.global_logistics.driver_negotiation.domain.DriverNegotiation;
import com.yotor.global_logistics.driver_negotiation.domain.dto.NegotiationStatus;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverNegotiationRepository extends CrudRepository<DriverNegotiation,Long> {

    @Query("""
            SELECT * FROM driver_negotiation dn 
            WHERE dn.public_id = :negotiationId
            """)
    Optional<DriverNegotiation> findByNegotiationId(UUID negotiationId);

    @Query("""
            SELECT COUNT(*) FROM driver_negotiation dn 
            WHERE dn.shipment_id = :shipmentId 
            AND dn.status = :status
            """)
    int countByShipmentAndStatus(UUID shipmentId, NegotiationStatus status);

    @Query("""
            SELECT * FROM driver_negotiation dn
            WHERE dn.shipment_id = :shipmentId
            AND dn.status = :status
            """)
    List<DriverNegotiation> findByShipmentAndStatus(UUID shipmentId, NegotiationStatus status);

    @Query("""
            SELECT EXISTS(
               SELECT 1 FROM driver_negotiation
               WHERE shipment_id = :shipmentId
               AND driver_id = :driverId
           )
           """)
    boolean existsByShipmentIdAndDriverId(UUID shipmentId, UUID driverId);

    @Query("""
            SELECT * FROM driver_negotiation dn
            WHERE dn.shipment_id = :shipmentId
            AND dn.status IN (:statusList) 
            """)
    List<DriverNegotiation> findExpiredNegotiations(
            @Param("shipmentId") UUID shipmentId,
            @Param("statusList") List<NegotiationStatus> statusList
    );

    @Query("""
            SELECT * FROM driver_negotiation
               WHERE shipment_id = :shipmentId
               AND driver_id = :driverId
            """)
    Optional<DriverNegotiation> findByShipmentIdAndDriverId(UUID shipmentId, UUID driverId);

    @Query("""
        SELECT * FROM driver_negotiation dn
        WHERE dn.driver_id = :driverId
        AND dn.status IN (:statusList)
    """)
    List<DriverNegotiation> findOffersByDriverIdAndStatus(
            @Param("driverId") UUID driverId,
            @Param("statusList") List<NegotiationStatus> statusList
    );

    @Query("""
            SELECT * FROM driver_negotiation dn
            WHERE dn.shipment_id = :shipmentId
            """)
    List<DriverNegotiation> findByShipmentId(UUID shipmentId);
}
