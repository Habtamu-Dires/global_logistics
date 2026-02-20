package com.yotor.global_logistics.identity.profile;

import com.yotor.global_logistics.identity.profile.dto.CreateDriverProfileRequest;
import com.yotor.global_logistics.identity.profile.dto.DriverAdminView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/approve/{user-id}")
    public ResponseEntity<?> approveDriverProfile(
            @PathVariable("user-id") String userId
    ){
        driverProfileService.approveDriver(userId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/suspend/{user-id}")
    public ResponseEntity<?> suspendDriverProfile(
            @PathVariable("user-id") String userId,
            @RequestBody String remark
    ){
        driverProfileService.suspendDriver(userId, remark);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<DriverAdminView>> getAllDrivers(){
        var res =  driverProfileService.getAllDrivers();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{phone}")
    public ResponseEntity<DriverAdminView> getDriverByPhone(@PathVariable String phone){
        return ResponseEntity.ok(driverProfileService.getDriverByPhone(phone));
    }

}
