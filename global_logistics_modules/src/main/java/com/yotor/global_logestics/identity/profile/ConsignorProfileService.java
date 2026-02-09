package com.yotor.global_logestics.identity.profile;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import com.yotor.global_logestics.identity.domain.user.ConsignorProfile;
import com.yotor.global_logestics.identity.domain.user.UserIdentity;
import com.yotor.global_logestics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logestics.identity.persistence.ConsignorProfileRepository;
import com.yotor.global_logestics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logestics.identity.profile.dto.ConsignorAdminView;
import com.yotor.global_logestics.identity.profile.dto.CreateConsignorProfileRequest;
import com.yotor.global_logestics.security.SecurityUtils;
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
        UUID userId = SecurityUtils.currentUser().userExternalId();
        UserIdentity user = userRepo.findByExternalId(userId)
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
        UserIdentity user = userRepo.findByExternalId(userId)
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
        UserIdentity user = userRepo.findByExternalId(userId)
                .orElseThrow();

        user.reject(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ConsignorAdminView> getAllConsignors(){
        return userRepo.findAllConsignor();
    }
}
