package com.yotor.global_logestics.identity.persistence;

import com.yotor.global_logestics.identity.domain.otp_rate_limit.OtpRateLimit;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OtpRateLimitRepository extends CrudRepository<OtpRateLimit,Long> {
    Optional<OtpRateLimit> findByPhone(String phone);
}
