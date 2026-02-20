package com.yotor.global_logistics.identity.auth;

import com.yotor.global_logistics.identity.auth.dto.*;
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
    public void sendOtp(@RequestBody OtpRequest req) {
        otpService.sendOtp(req.phone());
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<AuthTokens> verify(@RequestBody VerifyOtpRequest req) {
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


}

