package com.niit.industrialgasalarmcorporate.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.expiration = expiration;
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            if (secret.isBlank()) {
                log.error("==============================================");
                log.error("!!! JWT 密钥未设置 — 每次重启所有 Token 将全部失效 !!!");
                log.error("!!! 生产环境必须设置 JWT_SECRET 环境变量             !!!");
                log.error("!!!（至少 256 位 / 32 字节随机字符串）               !!!");
                log.error("==============================================");
            } else {
                log.warn("JWT 密钥长度不足 ({} 位)，已自动生成随机 HS256 密钥。"
                        + "生产环境务必设置 jwt.secret 环境变量（至少 256 位随机字符串）。",
                        keyBytes.length * 8);
            }
            this.key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        } else {
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
    }

    public String generateToken(String userUuid, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userUuid)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserUuid(String token) {
        return parseToken(token).getSubject();
    }

    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    public String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    public long getExpirationMillis() {
        return expiration;
    }

    /**
     * 计算 Token 的剩余有效时间（毫秒），防止黑名单条目过期时间不准确。
     * 如果 Token 已过期返回 0。
     */
    public long getRemainingMillis(String token) {
        try {
            Claims claims = parseToken(token);
            long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
            return Math.max(remaining, 0);
        } catch (Exception e) {
            return 0;
        }
    }
}
