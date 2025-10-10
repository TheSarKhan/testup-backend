package com.exam.examapp.security.service.impl;

import com.exam.examapp.exception.custom.JwtException;
import com.exam.examapp.service.interfaces.CacheService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final CacheService cacheService;

    @Value("${jwt.access-token-expire-time}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refresh-token-expire-time}")
    private Long refreshTokenExpireTime;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpireTime);
    }

    public String generateRefreshToken(String username) {
        String refreshToken = generateToken(username, refreshTokenExpireTime);
        cacheService.saveContent("refresh_token_", username, refreshToken, refreshTokenExpireTime);
        return refreshToken;
    }

    private String generateToken(String username, Long expireTime) {
        return Jwts.builder().
                subject(username).
                issuedAt(new Date(System.currentTimeMillis())).
                expiration(new Date(System.currentTimeMillis() + expireTime)).
                signWith(getSecretKey(secretKey)).
                compact();
    }

    private SecretKey getSecretKey(String secretKey) {
        byte[] decode = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decode);
    }

    public boolean isValidToken(String token) {
        try {
            return extractClaims(token).
                    getExpiration().
                    after(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.
                    parser().
                    verifyWith(getSecretKey(secretKey)).
                    build().
                    parseSignedClaims(token).
                    getPayload();
        } catch (Exception e) {
            throw new JwtException("Token yanlışdır");
        }
    }
}
