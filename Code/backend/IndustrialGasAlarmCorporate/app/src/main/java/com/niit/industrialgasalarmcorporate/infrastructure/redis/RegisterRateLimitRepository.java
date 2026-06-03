package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class RegisterRateLimitRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IP_PREFIX = "register:ip:";
    private static final int IP_MAX_ATTEMPTS = 3;
    private static final int IP_WINDOW_SECONDS = 3600;

    public boolean tryAcquire(String ip) {
        String key = IP_PREFIX + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, IP_WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        return count != null && count <= IP_MAX_ATTEMPTS;
    }
}
