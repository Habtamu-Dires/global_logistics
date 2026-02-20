package com.yotor.global_logistics.assignment.persistence;

import com.yotor.global_logistics.assignment.domain.document.Gdn;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GdnRepository extends CrudRepository<Gdn,Long> {

    @Query("""
            SELECT * FROM gdn 
            WHERE assignment_id = :assignmentId
            AND status = 'ISSUED'
            """)
    Optional<Gdn> findActiveGdnByAssignmentId(Long assignmentId);
}
