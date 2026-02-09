package com.yotor.global_logestics.identity.auth.scheduled;

import com.yotor.global_logestics.identity.persistence.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpTask {

    private final OtpVerificationRepository otpRepo;

    @Scheduled(cron = "0 0 2 * * *")
    void removeVerifiedAndExpiredOtps(){
        log.info("Removing verified and expired otps");
        otpRepo.deleteVerifiedAndExpiredOtps();
    }
}
