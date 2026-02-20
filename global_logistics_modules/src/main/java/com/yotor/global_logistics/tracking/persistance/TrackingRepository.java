package com.yotor.global_logistics.tracking.persistance;

import com.yotor.global_logistics.tracking.entity.Tracking;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackingRepository extends CrudRepository<Tracking, Long> {

    @Query("""
        SELECT *
        FROM shipment_assignment_tracking
        WHERE assignment_id = :assignmentId
        ORDER BY recorded_at DESC
        LIMIT 1
    """)
    Optional<Tracking> findLatest(UUID assignmentId);


    @Query("""
        SELECT *
        FROM shipment_assignment_tracking
        WHERE assignment_id = :assignmentId
        ORDER BY recorded_at ASC
    """)
    List<Tracking> findAllByAssignment(UUID assignmentId);
}
