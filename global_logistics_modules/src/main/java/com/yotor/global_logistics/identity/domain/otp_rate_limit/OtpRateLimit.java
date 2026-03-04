package com.yotor.global_logistics.identity.domain.otp_rate_limit;

import com.yotor.global_logistics.common.TimeFormatUtil;
import com.yotor.global_logistics.exception.BusinessException;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.*;

import static com.yotor.global_logistics.exception.ErrorCode.OTP_BLOCKED;


@Table("otp_rate_limit")
public class OtpRateLimit {

    @Id
    private Long id;
    private String phone;

    private LocalDate windowStart;   // usually today
    private int sentCount;

    private int violationCount;      // escalations

    private Instant blockedUntil;

    public OtpRateLimit(){}

    /* ---------- Domain rules ---------- */
    @PersistenceCreator
    public OtpRateLimit(
            Long id,
            String phone,
            LocalDate windowStart,
            int sentCount,
            int violationCount,
            Instant blockedUntil
    ){
        this.id = id;
        this.phone = phone;
        this.windowStart = windowStart;
        this.sentCount = sentCount;
        this.violationCount = violationCount;
        this.blockedUntil = blockedUntil;
    }

    public static OtpRateLimit create(String phone) {
        OtpRateLimit limit = new OtpRateLimit();
        limit.phone = phone;
        limit.windowStart = LocalDate.now();
        limit.violationCount = 0;
        limit.blockedUntil = null;
        limit.sentCount = 1;
        return limit;
    }

    public void checkAllowed(Instant now) {
        if (blockedUntil != null && now.isBefore(blockedUntil)) {
            String blockedTime =
                    TimeFormatUtil.formatToMinute(blockedUntil);
            throw new BusinessException(
                    OTP_BLOCKED,
                    blockedTime
            );
        }
    }

    public void recordSend(Instant now) {
        resetWindowIfNeeded(now);

        sentCount++;

        if (sentCount > 5) { // example daily limit
            escalateBlock(now);
        }
    }

    private void resetWindowIfNeeded(Instant now) {
        ZoneId zoneId = TimeFormatUtil.zoneId;
        LocalDate today = now.atZone(zoneId).toLocalDate();
        if (!today.equals(windowStart)) {
            windowStart = today;
            sentCount = 1;
        }
    }

    private void escalateBlock(Instant now) {
        violationCount++;

        long blockDays = calculateBlockDays(violationCount);

//        blockedUntil = now.plusDays(blockDays);
        blockedUntil = now.plus(Duration.ofDays(blockDays));
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

