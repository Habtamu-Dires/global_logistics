package com.yotor.global_logestics.identity.domain.otp_rate_limit;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.yotor.global_logestics.exception.ErrorCode.OTP_BLOCKED;

@Builder
@Table("otp_rate_limit")
public class OtpRateLimit {

    @Id
    private Long id;
    private String phone;

    private LocalDate windowStart;   // usually today
    private int sentCount;

    private int violationCount;      // escalations

    private LocalDateTime blockedUntil;

    public OtpRateLimit(){

    }

    /* ---------- Domain rules ---------- */
    @PersistenceCreator
    public OtpRateLimit(
            Long id,
            String phone,
            LocalDate windowStart,
            int sentCount,
            int violationCount,
            LocalDateTime blockedUntil
    ){
        this.id = id;
        this.phone = phone;
        this.windowStart = windowStart;
        this.sentCount = sentCount;
        this.violationCount = violationCount;
        this.blockedUntil = blockedUntil;
    }

    public static OtpRateLimit create(String phone) {

        return OtpRateLimit.builder()
                .phone(phone)
                .sentCount(1)
                .violationCount(0)
                .build();
    }

    public void checkAllowed(LocalDateTime now) {
        if (blockedUntil != null && now.isBefore(blockedUntil)) {
            throw new BusinessException(
                    OTP_BLOCKED,
                    blockedUntil
            );
        }
    }

    public void recordSend(LocalDateTime now) {
        resetWindowIfNeeded(now);

        sentCount++;

        if (sentCount > 5) { // example daily limit
            escalateBlock(now);
        }
    }

    private void resetWindowIfNeeded(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        if (!today.equals(windowStart)) {
            windowStart = today;
            sentCount = 1;
        }
    }

    private void escalateBlock(LocalDateTime now) {
        violationCount++;

        long blockDays = calculateBlockDays(violationCount);

        blockedUntil = now.plusDays(blockDays);
        sentCount = 0;
    }

    private long calculateBlockDays(int violationCount) {
        // 1 → 1 day, 2 → 2 days, 3 → 4 days
        return Math.min(1L << (violationCount - 1), 30);
    }

    public void resetViolations() {
        this.violationCount = 0;
        this.blockedUntil = null;
    }

}

