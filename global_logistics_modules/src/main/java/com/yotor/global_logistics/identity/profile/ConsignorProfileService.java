package com.yotor.global_logistics.identity.profile;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.domain.user.ConsignorProfile;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logistics.identity.persistence.ConsignorProfileRepository;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.identity.profile.dto.ConsignorAdminView;
import com.yotor.global_logistics.identity.profile.dto.CreateConsignorProfileRequest;
import com.yotor.global_logistics.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsignorProfileService {

    private final UserIdentityRepository userRepo;
    private final ConsignorProfileRepository consignorRepo;

    @PreAuthorize("hasRole('CONSIGNOR')")
    @Transactional
    public void createConsignorProfile(
            CreateConsignorProfileRequest req
    ) {
        UUID userId = SecurityUtils.currentUser().userPublicId();
        UserIdentity user = userRepo.findByPublicId(userId)
                .orElseThrow();

        if(user.getStatus() == UserStatus.OTP_SENT){
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        ConsignorProfile consignorProfile = ConsignorProfile.create(
                user.getId(),
                req.businessName(),
                req.tradeLicence()
        );

        consignorRepo.save(consignorProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void approveConsignor(String userExternalId) {

        UUID userId = UUID.fromString(userExternalId);
        UserIdentity user = userRepo.findByPublicId(userId)
                .orElseThrow();

        if(!user.isConsignor()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_CONSIGNOR);
        }

        user.approve(); // user status

        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void rejectConsignor(String userExternalId, String remark) {

        UUID userId = UUID.fromString(userExternalId);
        UserIdentity user = userRepo.findByPublicId(userId)
                .orElseThrow();

        user.reject(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ConsignorAdminView> getAllConsignors(){
        return userRepo.findAllConsignor();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ConsignorAdminView getConsignorByPhone(String phone){
        return userRepo.findConsignorByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

}
