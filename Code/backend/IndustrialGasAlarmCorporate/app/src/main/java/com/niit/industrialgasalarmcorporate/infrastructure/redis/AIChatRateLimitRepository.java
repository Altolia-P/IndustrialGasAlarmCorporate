package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class AIChatRateLimitRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IP_PREFIX = "ai:rate:ip:";
    private static final int IP_MAX_COUNT = 10;
    private static final int IP_WINDOW_SECONDS = 60;

    public boolean tryAcquire(String ip) {
        String key = IP_PREFIX + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, IP_WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        return count != null && count <= IP_MAX_COUNT;
    }
}
