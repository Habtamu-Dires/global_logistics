package com.yotor.global_logistics.identity.application.identity;

import com.yotor.global_logistics.common.PageResponse;
import com.yotor.global_logistics.common.TextDto;
import com.yotor.global_logistics.identity.application.identity.dto.CreateAdminUserRequest;
import com.yotor.global_logistics.identity.application.identity.dto.RegisteredUsers;
import com.yotor.global_logistics.identity.application.identity.dto.UserProfile;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/identity")
@Tag(name = "identity")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminUser(
            @RequestBody @Valid CreateAdminUserRequest req
    ){
        identityService.createAdminUser(req);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/admins")
    public ResponseEntity<PageResponse<UserProfile>> getPageOfAdmins(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        var res = identityService.getPageOfAdmins(page,size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/registered-users")
    public ResponseEntity<PageResponse<RegisteredUsers>> getPageOfRegisteredUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        var res = identityService.getPageOfRegisteredUsers(page,size);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(){
        var res = identityService.getUserProfile();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{phone}")
    public ResponseEntity<UserProfile> getUserByPhone(@PathVariable String phone){
        UserProfile res = identityService.getUserByPhone(phone);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/activate/{public-id}")
    public ResponseEntity<?> activateUser(
            @PathVariable("public-id") UUID publicId
    ){
        identityService.activate(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/disable/{public-id}")
    public ResponseEntity<?> disableUser(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid TextDto remark
    ){
        identityService.disable(publicId, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/verify-phone/{public-id}")
    public ResponseEntity<?> verifyPhone(
            @PathVariable("public-id") UUID publicId
    ){
        identityService.verifyPhone(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/reject-user/{public-id}")
    public ResponseEntity<?> rejectUser(
            @PathVariable("public-id") UUID publicId
    ){
        identityService.reject(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/add-remark/{public-id}")
    public ResponseEntity<?> addUserRemark(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid TextDto remark
    ){
        identityService.addRemark(publicId, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateUserProfile(@RequestBody @Valid UserProfile profile){
        UserProfile res = identityService.updateProfile(profile);
        return ResponseEntity.ok(res);
    }


}
