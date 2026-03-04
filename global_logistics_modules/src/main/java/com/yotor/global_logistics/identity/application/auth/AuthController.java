package com.yotor.global_logistics.identity.application.auth;

import com.yotor.global_logistics.identity.application.auth.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "auth")
class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest req
    ) {
        var res = authService.register(req);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest req) {
        otpService.sendOtp(req.phone());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<AuthTokens> verify(@RequestBody @Valid VerifyOtpRequest req) {
        AuthTokens res = otpService.verify(req.phone(), req.code());
        return ResponseEntity.ok(res);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthTokens> login(@RequestBody AuthenticationRequest request){
        AuthTokens res = authService.login(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refresh(@RequestBody RefreshRequest request){
        AuthTokens res = authService.refresh(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<Void> forgetPassword(@RequestBody @Valid ForgetPasswordRequest request){
        authService.requestPasswordReset(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest req
    ){
        authService.changePassword(req);
        return ResponseEntity.accepted().build();
    }


}

