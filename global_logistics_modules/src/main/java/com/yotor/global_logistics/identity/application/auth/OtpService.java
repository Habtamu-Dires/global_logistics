package com.yotor.global_logistics.identity.application.auth;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.application.auth.dto.AuthTokens;
import com.yotor.global_logistics.identity.domain.opt.Otp;
import com.yotor.global_logistics.identity.domain.opt.OtpVerification;
import com.yotor.global_logistics.identity.domain.otp_rate_limit.OtpRateLimit;
import com.yotor.global_logistics.identity.domain.refresh_token.RefreshToken;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.event.OtpEvent;
import com.yotor.global_logistics.identity.persistence.OtpRateLimitRepository;
import com.yotor.global_logistics.identity.persistence.OtpVerificationRepository;
import com.yotor.global_logistics.identity.persistence.RefreshTokenRepository;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpVerificationRepository otpRepo;
    private final JwtService jwtService;
    private final UserIdentityRepository userRepo;
    private final OtpRateLimitRepository otpRateLimitRepo;
    private final ApplicationEventPublisher publisher;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;

    public void sendInitialOtp(String phone){
        Otp otp = Otp.generate();
        OtpVerification otpVerification = OtpVerification.create(
                phone,
                otp.value()
        );

        otpRepo.save(otpVerification);

        //sms send
        Long expiresInMinutes = Duration.between(otpVerification.getExpiresAt(),Instant.now()).toMinutes();

        publisher.publishEvent(new OtpEvent(
                phone,
                otpVerification.getCode(),
                expiresInMinutes
        ));

        // reate limit
        OtpRateLimit otpRateLimit = OtpRateLimit.create(phone);
        otpRateLimitRepo.save(otpRateLimit);
    }

    public AuthTokens verify(String phone, String inputOtp) {

        // find user by phone
        UserIdentity user = userRepo.findByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_CREDENTIALS));

        OtpVerification verification = otpRepo.findCurrentByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.OTP_INVALID));

        IO.println("input otp " + inputOtp);
        IO.println("verification code " + verification.getCode());

        verification.verify(inputOtp);
        otpRepo.save(verification);

        user.verifyPhoneNumber();
        user.markVerified();
        userRepo.save(user);

        Set<String> roles = user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet());

        //authenticate
        String accessToken = jwtService
                .generateAccessToken(user.getPublicId(), roles);
        String refreshToken = jwtService
                .generateRefreshToken(user.getPublicId());

        String refreshTokenHash = tokenHasher.hash(refreshToken);

        RefreshToken storedRefreshToken = RefreshToken.initial(
                user.getPublicId(),
                refreshTokenHash
        );

        refreshTokenRepository.save(storedRefreshToken);

        return new AuthTokens(accessToken,refreshToken, user.isTempPassword());
    }


    @Transactional
    public void sendOtp(String phone) {

        UserIdentity userIdentity = userRepo.findByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(userIdentity.isPhoneVerified()){
            throw new BusinessException(ErrorCode.OTP_ALREADY_VERIFIED);
        }

        OtpRateLimit limit = otpRateLimitRepo.findByPhone(phone)
                .orElseGet(() -> OtpRateLimit.create(phone));

        limit.checkAllowed(Instant.now());

        Optional<OtpVerification> activeOtp =
                otpRepo.findActiveByPhone(phone);

        OtpVerification otp;

        if (activeOtp.isPresent()) {
            IO.println("active otp" + activeOtp.get().getCode());
            otp = activeOtp.get();
            otp.resend();
            IO.println("new otp" + otp.getCode());
        } else {
            Otp newOtp = Otp.generate();
            String code = newOtp.value();
            otp = OtpVerification.create(phone, code);
        }

        otpRepo.save(otp);
        // sned otp
        Long expiresInMinutes = Duration.between(otp.getExpiresAt(),Instant.now()).toMinutes();
        IO.println("Otp value " + otp.getCode());
        publisher.publishEvent(new OtpEvent(otp.getPhone(), otp.getCode(), expiresInMinutes));

        // otp rate limit
        limit.recordSend(Instant.now());
        otpRateLimitRepo.save(limit);
    }

    @Transactional
    public void sendPasswordResetOtp(String phone) {

        userRepo.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        OtpRateLimit limit = otpRateLimitRepo.findByPhone(phone)
                .orElseGet(() -> OtpRateLimit.create(phone));

        limit.checkAllowed(Instant.now());

        Optional<OtpVerification> activeOtp =
                otpRepo.findActiveByPhone(phone);

        OtpVerification otp;

        if (activeOtp.isPresent()) {
            otp = activeOtp.get();
            otp.resend();
        } else {
            Otp newOtp = Otp.generate();
            otp = OtpVerification.create(
                    phone,
                    newOtp.value()
            );
        }

        otpRepo.save(otp);

        Long expiresInMinutes = Duration.between(otp.getExpiresAt(),Instant.now()).toMinutes();
        publisher.publishEvent(
                new OtpEvent(otp.getPhone(), otp.getCode(), expiresInMinutes)
        );

        limit.recordSend(Instant.now());
        otpRateLimitRepo.save(limit);
    }

    @Transactional
    public void verifyPasswordResetOtp(String phone, String inputOtp) {

        OtpVerification verification =
                otpRepo.findCurrentByPhone(phone)
                        .orElseThrow(() -> new BusinessException(ErrorCode.OTP_INVALID));

        verification.verify(inputOtp);

        otpRepo.save(verification);
    }
}
