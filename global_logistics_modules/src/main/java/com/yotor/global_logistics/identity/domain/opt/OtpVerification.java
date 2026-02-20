package com.yotor.global_logistics.identity.domain.opt;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Table("otp_verification")
public class OtpVerification {

    @Id
    private  Long id;
    private  String phone;

    private String code;
    private LocalDateTime expiresAt;

    private int resendCount;
    private LocalDateTime lastSentAt;

    private int attempts;

    private OtpStatus status;

    private static final int MAX_ATTEMPTS = 5;

    private OtpVerification(){

    }

    /*--- Domain rules --- */
    @PersistenceCreator
    private OtpVerification(
            Long id,
            String phone,
            String code,
            LocalDateTime expiresAt,
            Integer resendCount,
            LocalDateTime lastSentAt,
            int attempts,
            OtpStatus status

    ){
        this.id = id;
        this.phone = phone;
        this.code = code;
        this.expiresAt = expiresAt;
        this.resendCount = resendCount;
        this.lastSentAt = lastSentAt;
        this.attempts = attempts;
        this.status = status;
    }

    private OtpVerification(
            String phoneNumber,
            String code,
            LocalDateTime expiresAt
    ) {
        this.phone = phoneNumber;
        this.code = code;
        this.expiresAt = expiresAt;
        this.attempts = 0;
        this.status = OtpStatus.ACTIVE;
    }

    //factory method
    public static OtpVerification create(
            String phoneNumber,
            String code
    ) {
        return new OtpVerification(
                phoneNumber,
                code,
                LocalDateTime.now().plus(Duration.ofMinutes(3))
        );
    }

    public void verify(String otpCodeAttempts) {

        if (status == OtpStatus.VERIFIED) {
            throw new BusinessException(ErrorCode.OTP_ALREADY_VERIFIED);
        }

        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new BusinessException(ErrorCode.OTP_EXPIRED);
        }

        if (attempts >= MAX_ATTEMPTS) {
            throw new BusinessException(ErrorCode.OTP_MAX_ATTEMPTS_EXCEEDED);
        }

        attempts++;

        if (!this.code.equals(otpCodeAttempts)) {
            throw new BusinessException(ErrorCode.OTP_INVALID);
        }

        this.status = OtpStatus.VERIFIED;
    }

    public void resend() {

        if (status != OtpStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.OTP_NOT_ACTIVE);
        }

        if (resendCount >= 3) {
            throw new BusinessException(ErrorCode.OTP_RESEND_LIMIT_REACHED);
        }

        if (Duration.between(lastSentAt, LocalDateTime.now()).getSeconds() < 60) {
            throw new BusinessException(ErrorCode.OTP_RESEND_TOO_SOON);
        }

        this.code = Otp.generate().value();
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
        this.lastSentAt = LocalDateTime.now();
        this.resendCount++;
    }

}

