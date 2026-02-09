package com.yotor.global_logestics.identity.persistence;

import com.yotor.global_logestics.identity.domain.user.UserIdentity;
import com.yotor.global_logestics.identity.profile.dto.ConsignorAdminView;
import com.yotor.global_logestics.identity.profile.dto.DriverAdminView;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserIdentityRepository
        extends CrudRepository<UserIdentity, Long> {

    @Query("""
            SELECT * FROM app_user ui
            WHERE ui.external_id = :externalId
            """)
    Optional<UserIdentity> findByExternalId(UUID externalId);

    @Query("""
            SELECT * FROM app_user u
            WHERE u.phone = :phone
            LIMIT 1
            """)
    Optional<UserIdentity> findByPhone(String phone);


    @Query("""
            SELECT u.role FROM app_user u 
            WHERE u.external_id = :externalId 
            """)
    Optional<String> findRoleByExternalId(UUID externalId);

    @Query("""
        SELECT u.external_id, u.first_name, u.last_name, u.profile_pic, u.national_id
               d.licence_number, d.licence_document, d.region, d.status
        FROM app_user u
        JOIN driver_profile d ON d.user_id = u.id
    """)
    List<DriverAdminView> findAllDrivers();

    @Query("""
        SELECT u.external_id, u.first_name, u.last_name,
               d.business_name, d.trade_licence, u.status
        FROM app_user u
        JOIN consignor_profile d ON d.user_id = u.id
    """)
    List<ConsignorAdminView> findAllConsignor();

}
