package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.domain.user.ConsignorProfile;
import com.yotor.global_logistics.identity.application.consignor.dto.ConsignorProfileView;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConsignorProfileRepository extends CrudRepository<ConsignorProfile,Long> {


    @Query("""
        SELECT u.phone,u.public_id, u.first_name, u.last_name,
               d.business_name, d.trade_licence, u.status, u.remark, 
               u.created_at, u.updated_at
        FROM app_user u
        JOIN consignor_profile d ON d.user_id = u.id
        ORDER BY u.created_at DESC
        LIMIT :limit OFFSET :offset
    """)
    List<ConsignorProfileView> findPageOfConsignors(long limit, long offset);

    @Query("""
            SELECT COUNT(*) FROM app_user u
            JOIN consignor_profile d ON d.user_id = u.id
            """)
    long countConsignors();


    @Query("""
            SELECT u.phone,u.public_id, u.first_name, u.last_name,
               d.business_name, d.trade_licence, u.status,
               u.created_at, u.updated_at
            FROM app_user u
            JOIN consignor_profile d ON d.user_id = u.id
            WHERE u.phone = :phone
            LIMIT 1
            """)
    Optional<ConsignorProfileView> findConsignorByPhone(String phone);


    @Query("""
            SELECT EXISTS (
                SELECT 1 FROM consignor_profile 
                WHERE user_id = :userId
            )
            """)
    boolean existsByUserId(Long userId);
}
