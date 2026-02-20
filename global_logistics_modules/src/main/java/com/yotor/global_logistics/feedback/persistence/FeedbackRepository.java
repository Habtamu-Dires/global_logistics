package com.yotor.global_logistics.feedback.persistence;

import com.yotor.global_logistics.feedback.domain.Feedback;
import com.yotor.global_logistics.feedback.domain.FeedbackActorType;
import com.yotor.global_logistics.feedback.domain.FeedbackTargetType;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FeedbackRepository extends CrudRepository<Feedback,Long> {

    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM shipment_assignment
                WHERE assignment_id = :assignmentId
                AND given_by_actory_type = :givenBy
                AND target_actory_type = :target
            )
            """)
    boolean existsByAssignmentIdAndGivenByActorTypeAndTargetActorType(
            UUID assignmentId,
            FeedbackActorType givenBy,
            FeedbackTargetType target
    );
}
