package com.yotor.global_logistics.identity.auth;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.auth.dto.AuthTokens;
import com.yotor.global_logistics.identity.domain.opt.Otp;
import com.yotor.global_logistics.identity.domain.opt.OtpVerification;
import com.yotor.global_logistics.identity.domain.otp_rate_limit.OtpRateLimit;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.persistence.OtpRateLimitRepository;
import com.yotor.global_logistics.identity.persistence.OtpVerificationRepository;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.security.JwtService;
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
                .generateAccessToken(user.getPublicId(), user.getRole().toString());
        String refreshToken = jwtService
                .generateRefreshToken(user.getPublicId());

        return new AuthTokens(accessToken,refreshToken);
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
        IO.println("Otp send " + otp.getCode()  + " for phone number " + phone);
        limit.recordSend(LocalDateTime.now());
        otpRateLimitRepo.save(limit);
    }
}
