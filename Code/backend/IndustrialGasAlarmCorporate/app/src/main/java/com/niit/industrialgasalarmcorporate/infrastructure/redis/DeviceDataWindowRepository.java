package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DeviceDataWindowRepository {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String KEY_PREFIX = "device:window:";

    public void addDataPoint(String deviceUuid, String ruleUuid, double concentration, long timestampSeconds) {
        String key = KEY_PREFIX + deviceUuid + ":" + ruleUuid;
        stringRedisTemplate.opsForZSet().add(key, timestampSeconds + ":" + concentration, timestampSeconds);
        stringRedisTemplate.expire(key, Duration.ofHours(1));
    }

    public long countExceededInWindow(String deviceUuid, String ruleUuid, long windowStartSeconds) {
        String key = KEY_PREFIX + deviceUuid + ":" + ruleUuid;
        Long count = stringRedisTemplate.opsForZSet().count(key, windowStartSeconds, Double.POSITIVE_INFINITY);
        return count != null ? count : 0;
    }

    public void removeExpired(String deviceUuid, String ruleUuid, long beforeSeconds) {
        String key = KEY_PREFIX + deviceUuid + ":" + ruleUuid;
        stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, beforeSeconds);
    }
}
