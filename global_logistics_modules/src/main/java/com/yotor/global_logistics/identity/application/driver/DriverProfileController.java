package com.yotor.global_logistics.identity.application.driver;

import com.yotor.global_logistics.common.PageResponse;
import com.yotor.global_logistics.common.TextDto;
import com.yotor.global_logistics.identity.application.driver.dto.CreateDriverProfileRequest;
import com.yotor.global_logistics.identity.application.driver.dto.DriverProfileView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Tag(name = "drivers")
public class DriverProfileController {

    private final DriverProfileService driverProfileService;

    @PostMapping("/create")
    public ResponseEntity<?> createDriverProfile(
            @RequestBody @Valid CreateDriverProfileRequest req
    ) {

        driverProfileService.createDriverProfile(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/approve/{public-id}")
    public ResponseEntity<?> approveDriverProfile(
            @PathVariable("public-id") UUID publicId
    ){
        driverProfileService.approveDriver(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/reject/{public-id}")
    public ResponseEntity<?> rejectDriverProfile(
            @PathVariable("public-id") UUID publicID,
            @RequestBody @Valid TextDto remark
    ){
        driverProfileService.rejectDriverProfile(publicID, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/suspend/{public-id}")
    public ResponseEntity<?> suspendDriver(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid TextDto remark
    ){
        driverProfileService.suspendDriver(publicId, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/activate/{public-id}")
    public ResponseEntity<?> activateDriver(
            @PathVariable("public-id") UUID publicId
    ){
        driverProfileService.activateDriver(publicId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/all")
    public ResponseEntity<PageResponse<DriverProfileView>> getPageOfDrivers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        var res =  driverProfileService.getPageOfDrivers(page, size);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{phone}")
    public ResponseEntity<DriverProfileView> getDriverByPhone(@PathVariable String phone){
        return ResponseEntity.ok(driverProfileService.getDriverByPhone(phone));
    }

}
