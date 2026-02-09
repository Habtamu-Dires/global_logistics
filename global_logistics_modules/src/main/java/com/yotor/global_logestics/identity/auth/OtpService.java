package com.yotor.global_logestics.identity.auth;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import com.yotor.global_logestics.identity.auth.dto.AuthTokens;
import com.yotor.global_logestics.identity.domain.opt.Otp;
import com.yotor.global_logestics.identity.domain.opt.OtpVerification;
import com.yotor.global_logestics.identity.domain.otp_rate_limit.OtpRateLimit;
import com.yotor.global_logestics.identity.domain.user.UserIdentity;
import com.yotor.global_logestics.identity.persistence.OtpRateLimitRepository;
import com.yotor.global_logestics.identity.persistence.OtpVerificationRepository;
import com.yotor.global_logestics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logestics.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpVerificationRepository otpRepo;
    private final JwtService jwtService;

    private final UserIdentityRepository userRepo;
    private final OtpRateLimitRepository otpRateLimitRepo;

    public void sendNewOtp(String phone){
        Otp otp = Otp.generate();
        OtpVerification otpVerification = OtpVerification.create(
                phone,
                otp.value()
        );

        otpRepo.save(otpVerification);

        //sms send
        IO.println("Otp send " + otp.value() + " for phone number " + phone);

        // reate limit
        OtpRateLimit.create(phone);

    }

    public AuthTokens verify(String phone, String inputOtp) {

        OtpVerification verification = otpRepo.findActiveByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.OTP_INVALID));

        verification.verify(inputOtp);
        otpRepo.save(verification);

        // find user by phone
        UserIdentity user = userRepo.findByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.verifyPhoneNumber();
        user.markVerified();
        userRepo.save(user);

        //authenticate
        String accessToken = jwtService
                .generateAccessToken(user.getExternalId(), user.getRole().toString());
        String refreshToken = jwtService
                .generateRefreshToken(user.getExternalId());

        return new AuthTokens(accessToken,refreshToken);
    }


    @Transactional
    public void sendOtp(String phone) {

        OtpRateLimit limit = otpRateLimitRepo.findByPhone(phone)
                .orElseGet(() -> OtpRateLimit.create(phone));

        limit.checkAllowed(LocalDateTime.now());

        Optional<OtpVerification> activeOtp =
                otpRepo.findActiveByPhone(phone);

        OtpVerification otp;

        if (activeOtp.isPresent()) {
            otp = activeOtp.get();
            otp.resend();
        } else {
            Otp newOtp = Otp.generate();
            String code = newOtp.value();
            otp = OtpVerification.create(phone, code);
        }

        otpRepo.save(otp);
        //        smsSender.send(phone, otp.peekForDelivery());
        limit.recordSend(LocalDateTime.now());
        otpRateLimitRepo.save(limit);
    }
}
