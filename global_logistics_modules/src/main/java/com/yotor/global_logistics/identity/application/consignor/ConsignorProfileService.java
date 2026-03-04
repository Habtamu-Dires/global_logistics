package com.yotor.global_logistics.identity.application.consignor;

import com.yotor.global_logistics.common.PageResponse;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.domain.user.ConsignorProfile;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import com.yotor.global_logistics.identity.persistence.ConsignorProfileRepository;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.identity.application.consignor.dto.ConsignorProfileView;
import com.yotor.global_logistics.identity.application.consignor.dto.CreateConsignorProfileRequest;
import com.yotor.global_logistics.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        if(!user.getRoles().contains(UserRole.CONSIGNOR)){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_CONSIGNOR);
        }

        if(user.getStatus() == UserStatus.OTP_SENT){
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        if(consignorRepo.existsByUserId(user.getId())){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        ConsignorProfile consignorProfile = ConsignorProfile.create(
                user.getId(),
                req.businessName(),
                req.tradeLicence()
        );

        user.markPending();
        userRepo.save(user);
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
    public void rejectConsignor(UUID publicId, String remark) {

        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow();

        if(!user.isConsignor()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_CONSIGNOR);
        }
        user.reject(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void suspendConsignor(UUID publicId, String remark) {
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow();

        if(!user.isConsignor()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_CONSIGNOR);
        }
        user.suspend(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void activateConsignor(UUID publicId) {
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow();

        if(!user.isConsignor()){
            throw new BusinessException(ErrorCode.USER_EXISTS_BUT_NOT_CONSIGNOR);
        }
        user.activate();
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<ConsignorProfileView> getPageOfConsignors(int page, int size){
        Pageable pageable = PageRequest.of(page,size);

        List<ConsignorProfileView> res = consignorRepo
                .findPageOfConsignors(pageable.getPageSize(),pageable.getOffset());

        long total = consignorRepo.countConsignors();

        return PageResponse.toPage(res,total,pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ConsignorProfileView getConsignorByPhone(String phone){
        return consignorRepo.findConsignorByPhone(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }


}
