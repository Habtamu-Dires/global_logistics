package com.yotor.global_logistics.identity.application;

import com.yotor.global_logistics.identity.application.dto.UserSummary;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identity")
@Tag(name = "identity")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class IdentityController {

    private final IdentityService identityService;

    @GetMapping("/{phone}")
    public ResponseEntity<UserSummary> getUserByPhone(@PathVariable String phone){
        UserSummary res = identityService.getUserByPhone(phone);
        return ResponseEntity.ok(res);
    }
}
