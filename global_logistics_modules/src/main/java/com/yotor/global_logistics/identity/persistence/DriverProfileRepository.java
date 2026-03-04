package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.domain.user.DriverProfile;
import com.yotor.global_logistics.identity.application.driver.dto.DriverProfileView;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
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

    @Query("""
        SELECT u.phone,u.public_id, u.first_name, u.last_name, u.profile_pic, u.national_id,
               d.licence_number, d.licence_document, d.preferred_lanes, d.status, u.remark,
               u.created_at , u.updated_at
        FROM app_user u
        JOIN driver_profile d ON d.user_id = u.id
        WHERE u.phone = :phone
        LIMIT 1
    """)
    Optional<DriverProfileView> findDriverByPhone(String phone);

    @Query("""
        SELECT u.phone,u.public_id, u.first_name, u.last_name, u.profile_pic, u.national_id,
               d.licence_number, d.licence_document, d.preferred_lanes, d.status, u.remark,
               u.created_at, u.updated_at
        FROM app_user u
        JOIN driver_profile d ON d.user_id = u.id
        ORDER BY u.created_at DESC
        LIMIT :limit OFFSET :offset
    """)
    List<DriverProfileView> findPageOfDriverAdminViews(long limit, long offset);

    @Query("""
            SELECT COUNT(*) FROM app_user u
            JOIN driver_profile d ON d.user_id = u.id
        """)
    long countDrivers();

    @Query("""
            SELECT EXISTS (
              SELECT 1 FROM driver_profile 
              WHERE user_id = :userId
            )
            """)
    boolean existsByUserId(Long userId);
}
