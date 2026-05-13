package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class JwtBlacklistRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_KEY = "jwt:blacklist";

    public void add(String token, long ttlMillis) {
        redisTemplate.opsForSet().add(BLACKLIST_KEY, token);
        redisTemplate.expire(BLACKLIST_KEY, ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLACKLIST_KEY, token));
    }
}
