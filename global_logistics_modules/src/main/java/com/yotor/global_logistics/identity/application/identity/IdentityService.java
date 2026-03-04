package com.yotor.global_logistics.identity.application.identity;

import com.yotor.global_logistics.common.PageResponse;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.application.identity.dto.CreateAdminUserRequest;
import com.yotor.global_logistics.identity.application.identity.dto.RegisteredUsers;
import com.yotor.global_logistics.identity.application.identity.dto.UserProfile;
import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.event.PasswordEvent;
import com.yotor.global_logistics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logistics.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final UserIdentityRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void createAdminUser(CreateAdminUserRequest req){
            checkUserPhoneNumber(req.phone());
            String password = generateRandomPassword(8);
            String passwordHash = passwordEncoder.encode(password);

        UserIdentity user = UserIdentity.register(
                req.firstName(),
                req.lastName(),
                req.phone(),
                Set.of(UserRole.ADMIN),
                passwordHash
        );
        user.verifyPhoneNumber();
        user.setPasswordAsTemp();
        user.activate();
        userRepo.save(user);

        // send password
        publisher.publishEvent(
            PasswordEvent.builder()
                    .phone(req.phone())
                    .password(password)
                    .reason("New Admin User")
                    .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void addRemark(UUID publicId, String remark){
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.addRemark(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserProfile getUserByPhone(String phone){
        UserIdentity userIdentity = userRepo.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserProfile.from(userIdentity);
    }

    public UserProfile getUserProfile(){
        UUID userId = SecurityUtils.currentUser().userPublicId();
        UserIdentity user = userRepo.findByPublicId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserProfile.from(user);
    }

    public UserProfile updateProfile(UserProfile profile){
        UUID loggedId = SecurityUtils.currentUser().userPublicId();
        if(!loggedId.equals(profile.publicId())){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        UserIdentity user = userRepo.findByPublicId(loggedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.update(
                profile.nationalId(),
                profile.profilePic(),
                profile.firstName(),
                profile.lastName(),
                profile.phone()
        );

        userRepo.save(user);
        return UserProfile.from(user);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public PageResponse<UserProfile> getPageOfAdmins(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        var res = userRepo.getPageOfAdmins(pageable.getPageSize(), pageable.getOffset());
        var userProfileList = res.stream().map(UserProfile::from).toList();

        long total = userRepo.countAdmins();

        return PageResponse.toPage(userProfileList, total,pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<RegisteredUsers> getPageOfRegisteredUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var res = userRepo.findRegisteredUsers(pageable.getPageSize(),pageable.getOffset());
        long total = userRepo.countRegisteredUsers();

        return PageResponse.toPage(res,total,pageable);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void activate(UUID publicId) {
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.activate();
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void disable(UUID publicId, String remark) {
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.disable(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void verifyPhone(UUID publicId) {
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.verifyPhoneNumber();
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void reject(UUID publicId) {
        UserIdentity user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.reject("Rejected by Admin");
        userRepo.save(user);
    }


    // helper methods
    private void checkPasswords(final String password,
                                final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private void checkUserPhoneNumber(final String phoneNumber) {
        this.userRepo.findByPhone(phoneNumber)
                .ifPresent(userIdentity -> {
                    throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
                });
    }

    // generate password
    private String generateRandomPassword(int length) {
        if (length < 4) throw new IllegalArgumentException("Length must be at least 4");

         String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
         String LOWER = "abcdefghijklmnopqrstuvwxyz";
         String DIGITS = "0123456789";
         String SPECIAL = "!@#$%^&*()-_=+[]{}";
         String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;


        // SecureRandom is the standard for cryptographic security
        RandomGenerator random = new SecureRandom();

        // 1. Ensure at least one of each required type
        StringBuilder password = new StringBuilder();
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // 2. Fill the rest of the length with random characters from the full pool
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // 3. Shuffle so the required characters aren't always at the start
        List<Character> letters = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(letters);

        return letters.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
