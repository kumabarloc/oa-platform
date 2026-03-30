package com.oa.common.core.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtUtils {

    private static final String CLAIM_KEY_USER_ID = "userId";
    private static final String CLAIM_KEY_USERNAME = "userName";
    private static final String CLAIM_KEY_DEPT_ID = "deptId";

    @Value("${security.jwt.secret:oa-platform-secret-key-must-be-at-least-32-chars}")
    private String secret;

    @Value("${security.jwt.expiration:7200000}")
    private Long expiration;

    /**
     * 生成 Token
     */
    public String generateToken(LoginUser loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, loginUser.getUserId());
        claims.put(CLAIM_KEY_USERNAME, loginUser.getUserName());
        claims.put(CLAIM_KEY_DEPT_ID, loginUser.getDeptId());
        return createToken(claims);
    }

    /**
     * 创建 Token
     */
    private String createToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(claims)
                .subject(claims.get(CLAIM_KEY_USERNAME).toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT signature invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get(CLAIM_KEY_USER_ID).toString());
    }

    /**
     * 获取用户名
     */
    public String getUserName(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_KEY_USERNAME).toString();
    }

    /**
     * 获取部门ID
     */
    public Long getDeptId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get(CLAIM_KEY_DEPT_ID).toString());
    }

    /**
     * 判断Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 获取过期时间
     */
    public Date getExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 刷新 Token
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        claims.put(CLAIM_KEY_USER_ID, claims.get(CLAIM_KEY_USER_ID));
        claims.put(CLAIM_KEY_USERNAME, claims.get(CLAIM_KEY_USERNAME));
        claims.put(CLAIM_KEY_DEPT_ID, claims.get(CLAIM_KEY_DEPT_ID));
        return createToken(claims);
    }
}