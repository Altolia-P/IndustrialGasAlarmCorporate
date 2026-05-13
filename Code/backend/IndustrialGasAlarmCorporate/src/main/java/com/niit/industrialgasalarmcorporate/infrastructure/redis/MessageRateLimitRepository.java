package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class MessageRateLimitRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PHONE_PREFIX = "msg:phone:";
    private static final String IP_PREFIX = "msg:ip:";
    private static final int PHONE_WINDOW_SECONDS = 60;
    private static final int IP_MAX_COUNT = 3;
    private static final int IP_WINDOW_SECONDS = 60;

    public boolean tryAcquirePhone(String phone) {
        String key = PHONE_PREFIX + phone;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", PHONE_WINDOW_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    public boolean tryAcquireIp(String ip) {
        String key = IP_PREFIX + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, IP_WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        return count != null && count <= IP_MAX_COUNT;
    }
}
