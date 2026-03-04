package com.yotor.global_logistics.identity.application.consignor;

import com.yotor.global_logistics.common.PageResponse;
import com.yotor.global_logistics.common.TextDto;
import com.yotor.global_logistics.identity.application.consignor.dto.ConsignorProfileView;
import com.yotor.global_logistics.identity.application.consignor.dto.CreateConsignorProfileRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PutMapping("/approve/{public-id}")
    public ResponseEntity<?> approveConsignorProfile(
            @PathVariable("public-id") String publicId
    ){
        consignorProfileService.approveConsignor(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/reject/{public-id}")
    public ResponseEntity<?> rejectConsignorProfile(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid TextDto remark
            ){
        consignorProfileService.rejectConsignor(publicId, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/suspend/{public-id}")
    public ResponseEntity<?> suspendConsignorProfile(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid TextDto remark
    ){
        consignorProfileService.suspendConsignor(publicId, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/activate/{public-id}")
    public ResponseEntity<?> activateConsignorProfile(
            @PathVariable("public-id") UUID publicId
    ){
        consignorProfileService.activateConsignor(publicId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/all")
    public ResponseEntity<PageResponse<ConsignorProfileView>> getPageOfConsignors(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        var res =  consignorProfileService.getPageOfConsignors(page,size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{phone}")
    public ResponseEntity<ConsignorProfileView> getConsignorByPhone(@PathVariable String phone){
        return ResponseEntity.ok(consignorProfileService.getConsignorByPhone(phone));
    }
}
