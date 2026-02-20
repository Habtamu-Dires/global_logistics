package com.yotor.global_logistics.assignment.persistence;

import com.yotor.global_logistics.assignment.domain.document.DocumentSequence;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DocumentSequenceRepository extends CrudRepository<DocumentSequence,Long> {

    @Query("""
        SELECT *
        FROM document_sequence
        WHERE doc_type = :name
          AND year = :year
        FOR UPDATE
        """)
    Optional<DocumentSequence> findForUpdate(String name, int year);

}
