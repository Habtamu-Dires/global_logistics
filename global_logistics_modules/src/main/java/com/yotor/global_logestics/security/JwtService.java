package com.yotor.global_logestics.security;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


@Service
public class JwtService {

    public static final String TOKEN_TYPE = "token_type";
    public static final String ROLE = "role";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JwtService() throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey("keys/local-only/private_key.pem");
        this.publicKey = KeyUtils.loadPublicKey("keys/local-only/public_key.pem");
    }

    public String generateAccessToken(final UUID userExternalId, final String role) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "ACCESS_TOKEN",
                "role", role
        );
        return buildToken(userExternalId, claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(final UUID userExternalId) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "REFRESH_TOKEN");
        return buildToken(userExternalId, claims, this.refreshTokenExpiration);
    }

    public String buildToken(final UUID userExternalId, final Map<String, Object> claims, final long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userExternalId.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)
                .compact();
    }

    public boolean isTokenValid(final String token, final UUID expectedUserId) {
        final UUID userId = extractUserExternalId(token);
        return userId.equals(expectedUserId) && !isTokenExpired(token);
    }

    public UUID extractUserExternalId(final String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return extractClaims(token).get(ROLE, String.class);
    }

    private boolean isTokenExpired(final String token) {
        return extractClaims(token).getExpiration()
                .before(new Date());
    }

    public boolean isAccessToken(String token) {
        return "ACCESS_TOKEN".equals(
                extractClaims(token).get(TOKEN_TYPE, String.class)
        );
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH_TOKEN".equals(
                extractClaims(token).get(TOKEN_TYPE, String.class)
        );
    }


    private Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (final JwtException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }


}
