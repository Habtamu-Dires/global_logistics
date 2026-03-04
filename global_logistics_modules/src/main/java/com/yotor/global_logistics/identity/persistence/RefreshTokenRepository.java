package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.domain.refresh_token.RefreshToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    @Query("""
            SELECT * FROM refresh_token rt
            WHERE rt.token_hash = :tokenHash
            """)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            DELETE FROM refresh_token
            WHERE revoked = true
               OR expires_at < now();
            """)
    void deleteRevokedAndExpiredTokens();

    @Modifying
    @Query("""
        UPDATE refresh_token SET revoked = true
        WHERE user_public_id = :publicId
    """)
    void revokeAllByUserPublicId(UUID publicId);
}
