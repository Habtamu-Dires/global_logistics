package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.domain.opt.OtpVerification;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends CrudRepository<OtpVerification, Long> {

    @Query("""
            SELECT *
            FROM otp_verification
            WHERE phone = :phone
              AND status = 'ACTIVE'
              AND expires_at > now()
            LIMIT 1;
            """)
    Optional<OtpVerification> findActiveByPhone(String phone);

    @Query("""
            DELETE FROM otp_verification otp
            WHERE otp.otp_status = 'VERIFIED'
            and otp.expires_at < now()
            """)
    void deleteVerifiedAndExpiredOtps();

}
