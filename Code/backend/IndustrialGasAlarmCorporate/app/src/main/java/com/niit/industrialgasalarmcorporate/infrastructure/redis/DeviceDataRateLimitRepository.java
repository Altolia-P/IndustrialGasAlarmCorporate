package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class DeviceDataRateLimitRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DEVICE_PREFIX = "device-data:device:";
    private static final int DEVICE_MAX_COUNT = 60;
    private static final int DEVICE_WINDOW_SECONDS = 60;

    public boolean tryAcquire(String deviceUuid) {
        String key = DEVICE_PREFIX + deviceUuid;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, DEVICE_WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        return count != null && count <= DEVICE_MAX_COUNT;
    }
}
