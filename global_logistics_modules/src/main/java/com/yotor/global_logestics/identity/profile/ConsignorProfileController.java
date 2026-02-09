package com.yotor.global_logestics.identity.profile;

import com.yotor.global_logestics.identity.profile.dto.ConsignorAdminView;
import com.yotor.global_logestics.identity.profile.dto.CreateConsignorProfileRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consignors")
@RequiredArgsConstructor
@Tag(name = "consignors")
public class ConsignorProfileController {

    private final ConsignorProfileService consignorProfileService;

    @PostMapping("/create")
    public ResponseEntity<?> createConsignorProfile(
            @RequestBody @Valid CreateConsignorProfileRequest req)
    {
        consignorProfileService.createConsignorProfile(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/approve/{user-id}")
    public ResponseEntity<?> approveConsignorProfile(
            @PathVariable("user-id") String userId
    ){
        consignorProfileService.approveConsignor(userId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/suspend/{user-id}")
    public ResponseEntity<?> suspendConsignorProfile(
            @PathVariable("user-id") String userId,
            @RequestBody String remark
    ){
        consignorProfileService.rejectConsignor(userId, remark);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ConsignorAdminView>> getAllConsignors(){
        var res =  consignorProfileService.getAllConsignors();
        return ResponseEntity.ok(res);
    }
}
