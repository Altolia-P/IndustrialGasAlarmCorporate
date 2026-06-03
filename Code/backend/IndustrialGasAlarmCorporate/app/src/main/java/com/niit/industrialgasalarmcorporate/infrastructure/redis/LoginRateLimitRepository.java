package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class LoginRateLimitRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IP_PREFIX = "login:ip:";
    private static final int IP_MAX_ATTEMPTS = 10;
    private static final int IP_WINDOW_SECONDS = 60;

    public boolean tryAcquire(String ip) {
        String key = IP_PREFIX + ip;
        Boolean set = redisTemplate.opsForValue().setIfAbsent(key, 1, IP_WINDOW_SECONDS, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(set)) {
            return true;
        }
        Long count = redisTemplate.opsForValue().increment(key);
        return count != null && count <= IP_MAX_ATTEMPTS;
    }
}
