package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.domain.user.DriverProfile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverProfileRepository extends CrudRepository<DriverProfile,Long> {

    @Query("""
            SELECT * FROm driver_profile dp
            WHERE dp.user_id = :userId
            """)
    Optional<DriverProfile> findByUserId(Long userId);

    @Query("""
            SELECT * FROm driver_profile dp
            JOIN app_user u ON dp.user_id = u.id
            WHERE u.public_id = :userExternalId
            """)
    Optional<DriverProfile> findByPublicId(UUID userExternalId);
}
