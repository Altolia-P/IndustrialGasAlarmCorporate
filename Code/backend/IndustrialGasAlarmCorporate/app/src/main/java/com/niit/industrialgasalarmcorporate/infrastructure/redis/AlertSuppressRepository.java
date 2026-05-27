package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AlertSuppressRepository {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String KEY_PREFIX = "alert:suppress:";

    public boolean trySuppress(String deviceUuid, String ruleUuid, Duration cooldown) {
        String key = KEY_PREFIX + deviceUuid + ":" + ruleUuid;
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", cooldown);
        return Boolean.TRUE.equals(success);
    }

    public void removeSuppress(String deviceUuid, String ruleUuid) {
        String key = KEY_PREFIX + deviceUuid + ":" + ruleUuid;
        stringRedisTemplate.delete(key);
    }
}
