package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.application.identity.dto.RegisteredUsers;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserIdentityRepository
        extends CrudRepository<UserIdentity, Long> {

    @Query("""
            SELECT * FROM app_user ui
            WHERE ui.public_id = :publicId
            """)
    Optional<UserIdentity> findByPublicId(UUID publicId);

    @Query("""
            SELECT * FROM app_user u
            WHERE u.phone = :phone
            LIMIT 1
            """)
    Optional<UserIdentity> findByPhone(String phone);


    @Query("""
            SELECT UNNEST(u.roles) FROM app_user u 
            WHERE u.public_id = :publicId 
            """)
    Optional<List<String>> findRolesByPublicId(UUID publicId);

    @Query("""
            SELECT * FROM app_user 
            WHERE  'ADMIN' = ANY(roles) 
            OR 'SUPER_ADMIN' = ANY(roles)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    List<UserIdentity> getPageOfAdmins(long limit, long offset);

    @Query("""
            SELECT count(*) FROM app_user 
             WHERE  'ADMIN' = ANY(roles) 
            OR 'SUPER_ADMIN' = ANY(roles)
            """)
    long countAdmins();

    @Query("""
            SELECT u.public_id, u.first_name, u.last_name, u.phone, 
              u.roles, u.status, u.created_at 
            FROM app_user u 
            LEFT JOIN driver_profile dp ON u.id = dp.user_id
            LEFT JOIN consignor_profile cp ON u.id = cp.user_id
            WHERE 'ADMIN' <> ALL(roles)
            AND dp.user_id IS NULL
            AND cp.user_id IS NULL
            ORDER BY u.created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    List<RegisteredUsers> findRegisteredUsers(int limit, long offset);
    @Query("""
            SELECT count(*) FROM app_user u
            LEFT JOIN driver_profile dp ON u.id = dp.user_id
            LEFT JOIN consignor_profile cp ON u.id = cp.user_id
            WHERE 'ADMIN' <> ALL(roles)
            AND dp.user_id IS NULL
            AND cp.user_id IS NULL
            """)
    long countRegisteredUsers();
}
