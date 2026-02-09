package com.yotor.global_logestics.identity.domain.user;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import com.yotor.global_logestics.identity.domain.user.enums.UserRole;
import com.yotor.global_logestics.identity.domain.user.enums.UserStatus;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Table("app_user")
public class UserIdentity {

    @Id
    private Long id;

    private UUID externalId;

    private String phone;
    private String passwordHash;

    private UserRole role;

    private UserStatus status;

    private String firstName;
    private String lastName;
    private String nationalId;
    private String profilePic;

    private boolean phoneVerified;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String remark;


    protected UserIdentity(){}


    /* ---------- Domain behavior ---------- */

    // register
    public static UserIdentity register(
            String firstName,
            String lastName,
            String phone,
            UserRole role,
            String passwordHash
    ) {
        UserIdentity user = new UserIdentity();
        user.externalId = UUID.randomUUID();
        user.firstName = firstName;
        user.lastName = lastName;
        user.phone = phone;
        user.role = role;
        user.passwordHash = passwordHash;
        user.status = UserStatus.OTP_SENT;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }



    public void verifyPhoneNumber(){
        this.phoneVerified = true;
    }

    public void addProfilePic(String profile_pic){this.profilePic = profile_pic;}
    public void addNationalId(String nationalId) {this.nationalId = nationalId; }

    public void markVerified(){
        this.status = UserStatus.VERIFIED;
    }

    public void approve() {
        if (this.status != UserStatus.VERIFIED) {
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }
        this.status = UserStatus.APPROVED;
    }

    public void reject(String remark) {
        this.status = UserStatus.REJECTED;
        this.remark = remark;
    }

    public void addRemark(String remark){
        this.remark = remark;
    }

    public boolean isDriver() {
        return this.role == UserRole.DRIVER;
    }

    public boolean isConsignor() {
        return this.role == UserRole.CONSIGNOR;
    }

}
