package com.yotor.global_logestics.identity.auth;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import com.yotor.global_logestics.identity.auth.dto.*;
import com.yotor.global_logestics.identity.domain.refresh_token.RefreshToken;
import com.yotor.global_logestics.identity.domain.user.UserIdentity;
import com.yotor.global_logestics.identity.domain.user.enums.UserRole;
import com.yotor.global_logestics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logestics.identity.persistence.RefreshTokenRepository;
import com.yotor.global_logestics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logestics.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserIdentityRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final TokenHasher tokenHasher;


    // register
    public RegisterResponse register(RegisterRequest req) {

        // user can not be registered as ADMIN
        if(req.role() == UserRole.ADMIN){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        checkUserPhoneNumber(req.phoneNumber());
        checkPasswords(req.password(),req.confirmPassword());
        String passwordHash = passwordEncoder.encode(req.password());

        UserIdentity user = UserIdentity.register(
                req.firstName(),
                req.lastName(),
                req.phoneNumber(),
                req.role(),
                passwordHash
        );

        userRepo.save(user);

        otpService.sendNewOtp(user.getPhone());

        return new RegisterResponse(user.getExternalId(), user.getPhone());
    }


    //login
    public AuthTokens login(AuthenticationRequest request) {

        UserIdentity user = userRepo.findByPhone(request.phone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        verifyPassword(request.password(), user);
        verifyUserStatus(user);

        String accessToken = jwtService.generateAccessToken(
                user.getExternalId(),
                user.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getExternalId()
        );

        String refreshTokenHash = tokenHasher.hash(refreshToken);

        RefreshToken storedRefreshToken = RefreshToken.initial(
                user.getExternalId(),
                refreshTokenHash
        );

        refreshTokenRepo.save(storedRefreshToken);

        return new AuthTokens(accessToken, refreshToken);
    }

    // refresh
    public AuthTokens refresh(RefreshRequest req) {

        String hash = tokenHasher.hash(req.refreshToken());

        RefreshToken current = refreshTokenRepo.findByTokenHash(hash)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (current.isRevoked()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        if(current.isExpired()){
           throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (current.isAbsoluteExpired()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_ABSOLUTE_EXPIRED);
        }

        String newRawToken = jwtService.generateRefreshToken(
                current.userExternalId()
        );

        RefreshToken rotated = current.rotate(
                tokenHasher.hash(newRawToken)
        );

        refreshTokenRepo.save(current);   // revoked
        refreshTokenRepo.save(rotated);   // new token

        String userRole = userRepo.findRoleByExternalId(current.userExternalId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND));

        String accessToken = jwtService.generateAccessToken(
                current.userExternalId(),
                userRole
        );

        return new AuthTokens(accessToken, newRawToken);
    }


    private void verifyPassword(String loginPassword, UserIdentity user){
        if (!passwordEncoder.matches(
                loginPassword,
                user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }
    }

    private void verifyUserStatus(UserIdentity user){
        if (!user.isPhoneVerified()) {
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }

    }

    private void checkPasswords(final String password,
                                final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private void checkUserPhoneNumber(final String phoneNumber) {
         this.userRepo.findByPhone(phoneNumber)
                .ifPresent(userIdentity -> {
                    throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
                });
    }

    public void logout(LogoutRequest request){
        String hash = tokenHasher.hash(request.refreshToken());

        refreshTokenRepo.findByTokenHash(hash)
                .ifPresent(token -> {
                    RefreshToken refreshToken = token.withRevoked();
                    refreshTokenRepo.save(refreshToken);
                });
    }

}
