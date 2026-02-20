package com.yotor.global_logistics.identity.application;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.application.dto.UserSummary;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class IdentityService {

    private final UserIdentityRepository userRepo;

    public UserSummary getUserByPhone(String phone){
        UserIdentity userIdentity = userRepo.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserSummary.from(userIdentity);
    }
}
