package com.yotor.global_logistics.identity.application.auth;

import com.yotor.global_logistics.identity.application.auth.dto.LogoutRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logout")
@Tag(name = "logout")
public class LogoutController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request){
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
