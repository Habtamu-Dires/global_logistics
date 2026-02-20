package com.yotor.global_logistics.assignment.persistence;

import com.yotor.global_logistics.assignment.api.dto.AssignmentTrackingRes;
import com.yotor.global_logistics.assignment.domain.assignment.ShipmentAssignment;
import com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends CrudRepository<ShipmentAssignment,Long> {

    @Query("""
            SELECT * FROM shipment_assignment sa
            WHERE sa.public_id = :publicId
            """)
    Optional<ShipmentAssignment> findByPublicId(UUID publicId);

    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM shipment_assignment sa
                WHERE sa.shipment_id = :shipmentId
                AND sa.status = :status
            )
            """)
    boolean existsByShipmentIdAndStatus(UUID shipmentId, AssignmentStatus status);

    @Query("""
            SELECT COUNT(*) FROM shipment_assignment sa
            WHERE sa.shipment_id = :shipmentId 
            AND sa.status = :status
            """)
    int countByShipmentIdAndStatus(UUID shipmentId, AssignmentStatus status);


    @Query("""
            SELECT * FROM shipment_assignment sa
            WHERE sa.shipment_id = :shipmentId
            """)
    List<ShipmentAssignment> findByShipmentId(UUID shipmentId);

    @Query("""
            SELECT * FROM shipment_assignment sa
            WHERE sa.driver_id = :driverId
            """)
    List<ShipmentAssignment> findByDriverId(UUID driverId);

    @Query("""
            SELECT sa.driver_id, sa.status FROM shipment_assignment sa
            WHERE sa.public_id = :assignmentId
            """)
    Optional<AssignmentTrackingRes> findByPublicIdForTracking(UUID assignmentId);

    @Query("""
            SELECT sa.shipment_id FROM shipment_assignment sa
            WHERE sa.public_id = :assignmentId
            """)
    UUID findShipmentId(UUID assignmentId);

    @Query("""
            SELECT sa.driver_id FROM shipment_assignment sa
            WHERE sa.public_id = :assignmentId
            """)
    Optional<UUID> findDriverId(UUID assignmentId);
}
