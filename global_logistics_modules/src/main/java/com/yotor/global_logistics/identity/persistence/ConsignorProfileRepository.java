package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.domain.user.ConsignorProfile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConsignorProfileRepository extends CrudRepository<ConsignorProfile,Long> {

    @Query("""
            SELECT * FROM consignor_profile cp
            WHERE cp.user_id = :userId
            """)
    Optional<ConsignorProfile> findByUserId(Long userId);
}
