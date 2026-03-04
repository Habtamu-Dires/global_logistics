package com.yotor.global_logistics.identity.application.identity;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.port.IdentityQueryPort;
import com.yotor.global_logistics.identity.application.identity.dto.UserProfile;
import com.yotor.global_logistics.identity.application.vehicle.dto.VehicleSummery;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.identity.persistence.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentityQueryPortImpl implements IdentityQueryPort {

    private final UserIdentityRepository userRepo;
    private final VehicleRepository vehicleRepo;

    @Override
    public UserProfile getUserSummary(UUID userPublicId) {
        return UserProfile.from(userRepo.findByPublicId(userPublicId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public VehicleSummery getVehicleSummery(UUID driverId){
        UserIdentity driver = userRepo.findByPublicId(driverId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return vehicleRepo.findVehicleSummeryByDriverId(driver.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));
    }

    @Override
    public boolean isDriverApproved(UUID userPublicId) {
        return userRepo.findByPublicId(userPublicId)
                .map(u -> u.getStatus() == UserStatus.APPROVED && u.isDriver())
                .orElse(false);
    }

    @Override
    public boolean isConsignorApproved(UUID userPublicId) {
        return userRepo.findByPublicId(userPublicId)
                .map(u -> u.getStatus() == UserStatus.APPROVED && u.isConsignor())
                .orElse(false);
    }

}
