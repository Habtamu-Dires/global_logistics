package com.yotor.global_logestics.identity.auth;

import com.yotor.global_logestics.identity.domain.otp_rate_limit.OtpRateLimit;
import com.yotor.global_logestics.identity.persistence.OtpRateLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpRateLimitService {

    private final OtpRateLimitRepository otpRateLimitRepo;

    @PreAuthorize("hasRole('ADMIN')")
    public void unblock(String phone) {
        OtpRateLimit limit = otpRateLimitRepo.findByPhone(phone).orElseThrow();
        limit.resetViolations();
    }

}
