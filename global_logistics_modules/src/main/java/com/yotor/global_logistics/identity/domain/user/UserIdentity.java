package com.yotor.global_logistics.identity.domain.user;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Table("app_user")
public class UserIdentity {

    @Id
    private Long id;

    private UUID publicId;

    private String phone;
    private String passwordHash;
    private boolean isTempPassword;

    private Set<UserRole> roles;

    private UserStatus status;

    private String firstName;
    private String lastName;
    private String nationalId;
    private String profilePic;

    private boolean phoneVerified;

    private Instant createdAt;
    private Instant updatedAt;

    private String remark;


    protected UserIdentity(){}


    /* ---------- Domain behavior ---------- */

    // register
    public static UserIdentity register(
            String firstName,
            String lastName,
            String phone,
            Set<UserRole> roles,
            String passwordHash
    ) {
        UserIdentity user = new UserIdentity();
        user.publicId = UUID.randomUUID();
        user.firstName = firstName;
        user.lastName = lastName;
        user.phone = phone;
        user.roles = roles;
        user.passwordHash = passwordHash;
        user.isTempPassword = false;
        user.status = UserStatus.OTP_SENT;
        user.createdAt = Instant.now();
        user.updatedAt = Instant.now();

        return user;
    }

    // update
    public void update(
            String nationalId,
            String profilePic,
            String firstName,
            String lastName,
            String phone
    ) {
        this.nationalId = nationalId;
        this.profilePic = profilePic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.updatedAt = Instant.now();
    }

    public void verifyPhoneNumber(){
        this.phoneVerified = true;
        this.markVerified();
    }

    public void addProfilePic(String profile_pic){this.profilePic = profile_pic;}
    public void addNationalId(String nationalId) {this.nationalId = nationalId; }

    public void markVerified(){
        this.status = UserStatus.VERIFIED;
        this.updatedAt = Instant.now();
    }

    public void markPending(){
        this.status = UserStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public void approve() {
        if (this.status != UserStatus.PENDING
                && this.status != UserStatus.VERIFIED
                && this.status != UserStatus.REJECTED
                && this.status != UserStatus.SUSPENDED
        ) {
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }
        this.status = UserStatus.APPROVED;
        this.updatedAt = Instant.now();
    }

    public void reject(String remark) {
        this.status = UserStatus.REJECTED;
        this.updatedAt = Instant.now();
        this.remark = remark;
    }

    public void suspend(String remark){
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = Instant.now();
        this.remark = remark;
    }

   public void disable(String remark){
        this.status = UserStatus.DISABLED;
        this.updatedAt = Instant.now();
        this.remark = remark;
   }

   public void activate(){
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
        this.remark = "";
   }

    public boolean isDriver() {
        return this.roles.contains(UserRole.DRIVER);
    }

    public boolean isConsignor() {
        return this.roles.contains(UserRole.CONSIGNOR);
    }

    public void changePassword(String newHash) {
        this.passwordHash = newHash;
        this.updatedAt = Instant.now();
    }
    public void addRemark(String remark) {
        this.remark = remark;
        this.updatedAt = Instant.now();
    }


    public void setPasswordAsTemp() {this.isTempPassword = true;}
    public void setPasswordAsNotTemp() {this.isTempPassword = false;}
}
