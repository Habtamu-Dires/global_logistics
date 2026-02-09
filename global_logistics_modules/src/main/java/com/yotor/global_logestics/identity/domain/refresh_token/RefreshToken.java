package com.yotor.global_logestics.identity.domain.refresh_token;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Table("refresh_token")
public class RefreshToken {

    @Id
    private Long id;
    private final UUID userExternalId;

    private final String tokenHash;

    private final Instant issuedAt;     // absolute start
    private final Instant expiresAt;    // sliding expiry

    @Getter
    private final boolean revoked;

    // constants (policy-level, not magic numbers)
    public static final Duration ABSOLUTE_MAX_LIFETIME = Duration.ofDays(90);
    public static final Duration ROTATION_TTL = Duration.ofDays(30);


    /* ---------- Domain rules ---------- */
    @PersistenceCreator // Tells Spring to use THIS constructor when reading from DB
    private RefreshToken(
            Long id,
            UUID userExternalId,
            String tokenHash,
            Instant issuedAt,
            Instant expiresAt,
            boolean revoked
    ) {
        this.id = id;
        this.userExternalId = userExternalId;
        this.tokenHash = tokenHash;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    private RefreshToken(
            UUID userExternalId,
            String tokenHash,
            Instant issuedAt,
            Instant expiresAt,
            boolean revoked
    ) {
        this.userExternalId = userExternalId;
        this.tokenHash = tokenHash;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public static RefreshToken initial(
            UUID userExternalId,
            String tokenHash
    ) {
        return new RefreshToken(
                userExternalId,
                tokenHash,
                Instant.now(),
                Instant.now().plus(ROTATION_TTL),
                false
        );
    }

    public RefreshToken rotate(
            String newTokenHash
    ) {
        if (isRevoked()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        if (isExpired()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (isAbsoluteExpired()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_ABSOLUTE_EXPIRED);
        }

        // revoke current token
        this.withRevoked();

        // issue new token with SAME issuedAt
        return new RefreshToken(
                this.userExternalId,
                newTokenHash,
                this.issuedAt,
                Instant.now().plus(ROTATION_TTL),
                false
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isAbsoluteExpired() {
        return Instant.now().isAfter(issuedAt.plus(ABSOLUTE_MAX_LIFETIME));
    }

    public RefreshToken withRevoked() {
        // We return a NEW instance, but we KEEP the same ID
        // This tells Spring Data JDBC to perform an UPDATE, not an INSERT
        return new RefreshToken(
                this.id,
                this.userExternalId,
                this.tokenHash,
                this.issuedAt,
                this.expiresAt,
                true
        );
    }

    public UUID userExternalId() {
        return userExternalId;
    }
}
