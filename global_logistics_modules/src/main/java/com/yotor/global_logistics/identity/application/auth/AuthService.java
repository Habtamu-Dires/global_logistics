package com.yotor.global_logistics.identity.application.auth;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.application.auth.dto.*;
import com.yotor.global_logistics.identity.domain.refresh_token.RefreshToken;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logistics.identity.persistence.RefreshTokenRepository;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional
    public RegisterResponse register(RegisterRequest req) {

        // user can't be registered as ADMIN or Super Admin
        if(req.role() == UserRole.ADMIN || req.role() == UserRole.SUPER_ADMIN){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        checkUserPhoneNumber(req.phoneNumber());
        checkPasswords(req.password(),req.confirmPassword());
        String passwordHash = passwordEncoder.encode(req.password());

        UserIdentity user = UserIdentity.register(
                req.firstName(),
                req.lastName(),
                req.phoneNumber(),
                Set.of(req.role()),
                passwordHash
        );

        userRepo.save(user);

        otpService.sendInitialOtp(user.getPhone());

        return new RegisterResponse(user.getPublicId(), user.getPhone());
    }


    //login
    @Transactional
    public AuthTokens login(AuthenticationRequest request) {

        UserIdentity user = userRepo.findByPhone(request.phone())
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_CREDENTIALS));

        verifyPassword(request.password(), user);
        verifyUserStatus(user);


        Set<String> roles = user.getRoles().stream()
                .map(UserRole::name).collect(Collectors.toSet());
        String accessToken = jwtService.generateAccessToken(
                user.getPublicId(),
                roles
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getPublicId()
        );

        String refreshTokenHash = tokenHasher.hash(refreshToken);

        RefreshToken storedRefreshToken = RefreshToken.initial(
                user.getPublicId(),
                refreshTokenHash
        );

        refreshTokenRepo.save(storedRefreshToken);

        return new AuthTokens(accessToken, refreshToken, user.isTempPassword());
    }

    // refresh
    @Transactional
    public AuthTokens refresh(RefreshRequest req) {

        String hash = tokenHasher.hash(req.refreshToken());

        RefreshToken current = refreshTokenRepo.findByTokenHash(hash)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        UserIdentity user = userRepo.findByPublicId(current.userPublicId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //verify user status
        verifyUserStatus(user);

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
                current.userPublicId()
        );

        RefreshToken rotated = current.rotate(
                tokenHasher.hash(newRawToken)
        );

        refreshTokenRepo.save(current);   // revoked
        refreshTokenRepo.save(rotated);   // new token

        List<String> userRoles = userRepo.findRolesByPublicId(current.userPublicId())
                .stream()
                .flatMap(Collection::stream)
                .toList();

        String accessToken = jwtService.generateAccessToken(
                current.userPublicId(),
                new HashSet<>(userRoles)
        );

        return new AuthTokens(accessToken, newRawToken, false);
    }


    public void logout(LogoutRequest request){
        String hash = tokenHasher.hash(request.refreshToken());

        refreshTokenRepo.findByTokenHash(hash)
                .ifPresent(token -> {
                    RefreshToken refreshToken = token.withRevoked();
                    refreshTokenRepo.save(refreshToken);
                });
    }

    // forget password ...
    public void requestPasswordReset(ForgetPasswordRequest req) {
        otpService.sendPasswordResetOtp(req.phone());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {

        UserIdentity user = userRepo.findByPhone(req.phone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkPasswords(req.newPassword(), req.confirmPassword());

        otpService.verifyPasswordResetOtp(
                req.phone(),
                req.otpCode()
        );

        String newHash = passwordEncoder.encode(req.newPassword());

        user.changePassword(newHash);

        userRepo.save(user);

        refreshTokenRepo.revokeAllByUserPublicId(user.getPublicId());
    }

    // change password
    public void changePassword(ChangePasswordRequest req) {
        UserIdentity user = userRepo.findByPhone(req.phone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        verifyPassword(req.currentPassword(), user);
        String newHash = passwordEncoder.encode(req.newPassword());
        user.setPasswordAsNotTemp();
        user.changePassword(newHash);
        userRepo.save(user);
    }

    /** helper methods ** */
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

        if(user.getStatus() == UserStatus.DISABLED){
            throw  new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
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

}
