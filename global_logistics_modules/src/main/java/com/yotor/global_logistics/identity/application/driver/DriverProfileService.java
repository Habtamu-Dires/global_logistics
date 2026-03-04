package com.yotor.global_logistics.identity.application.driver;

import com.yotor.global_logistics.common.PageResponse;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.domain.user.DriverProfile;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logistics.identity.persistence.DriverProfileRepository;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.identity.application.driver.dto.CreateDriverProfileRequest;
import com.yotor.global_logistics.identity.application.driver.dto.DriverProfileView;
import com.yotor.global_logistics.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if(!user.getRoles().contains(UserRole.DRIVER)){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_DRIVER);
        }

        if(user.getStatus() == UserStatus.OTP_SENT){
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        if(driverRepo.existsByUserId(user.getId())){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        DriverProfile driverProfile = DriverProfile.create(
                user.getId(),
                req.licenceNumber(),
                req.licenceDocument(),
                req.preferredLanes()
        );

        driverProfile.profileCreated();
        driverRepo.save(driverProfile);

        user.addProfilePic(req.profilePic());
        user.addNationalId(req.nationalId());

        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void approveDriver(UUID publicId) {

        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow();

        DriverProfile driver = driverRepo.findByUserId(user.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!user.isDriver()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_DRIVER);
        }

        user.approve(); // user status
        driver.approve();

        userRepo.save(user);
        driverRepo.save(driver);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void rejectDriverProfile(UUID publicId, String remark) {

        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow();

        DriverProfile driver = driverRepo.findByUserId(user.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!user.isDriver()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_DRIVER);
        }

        driver.reject();
        user.reject(remark);
        userRepo.save(user);
        driverRepo.save(driver);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void suspendDriver(UUID publicId, String remark) {

        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow();

        if(!user.isDriver()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_DRIVER);
        }

        DriverProfile driver = driverRepo.findByUserId(user.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        driver.suspend();
        user.suspend(remark);
        driverRepo.save(driver);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void activateDriver(UUID publicId) {
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow();
        if(!user.isDriver()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_DRIVER);
        }

        DriverProfile driver = driverRepo.findByUserId(user.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.activate();
        driver.activate();
        driverRepo.save(driver);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<DriverProfileView> getPageOfDrivers(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        var res = driverRepo.findPageOfDriverAdminViews(pageable.getPageSize(), pageable.getOffset());
        long total = driverRepo.countDrivers();

        return PageResponse.toPage(res,total,pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DriverProfileView getDriverByPhone(String phone){
        return driverRepo.findDriverByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
