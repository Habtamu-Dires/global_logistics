package com.yotor.global_logistics.identity.profile;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.domain.user.DriverProfile;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logistics.identity.persistence.DriverProfileRepository;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.identity.profile.dto.CreateDriverProfileRequest;
import com.yotor.global_logistics.identity.profile.dto.DriverAdminView;
import com.yotor.global_logistics.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverProfileService {
    private final UserIdentityRepository userRepo;
    private final DriverProfileRepository driverRepo;

    @PreAuthorize("hasRole('DRIVER')")
    @Transactional
    public void createDriverProfile(
            CreateDriverProfileRequest req
    ) {
        var userExternalId = SecurityUtils.currentUser().userPublicId();
        UserIdentity user = userRepo.findByPublicId(userExternalId)
                .orElseThrow();

        if(user.getStatus() == UserStatus.OTP_SENT){
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        DriverProfile driverProfile = DriverProfile.create(
                user.getId(),
                req.licenceNumber(),
                req.licenceDocument(),
                req.region()
        );

        driverRepo.save(driverProfile);

        user.addProfilePic(req.profilePic());
        user.addNationalId(req.nationalId());

        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void approveDriver(String userExternalId) {

        UUID userId = UUID.fromString(userExternalId);
        UserIdentity user = userRepo.findByPublicId(userId)
                .orElseThrow();

        DriverProfile driver = driverRepo.findByUserId(user.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));


        user.approve(); // user status
        driver.approve();

        userRepo.save(user);
        driverRepo.save(driver);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void suspendDriver(String userExternalId, String remark) {

        UUID userId = UUID.fromString(userExternalId);
        UserIdentity user = userRepo.findByPublicId(userId)
                .orElseThrow();

        DriverProfile driver = driverRepo.findByUserId(user.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        driver.suspend();
        driverRepo.save(driver);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<DriverAdminView> getAllDrivers(){
        return userRepo.findAllDrivers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DriverAdminView getDriverByPhone(String phone){
        return userRepo.findDriverByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
