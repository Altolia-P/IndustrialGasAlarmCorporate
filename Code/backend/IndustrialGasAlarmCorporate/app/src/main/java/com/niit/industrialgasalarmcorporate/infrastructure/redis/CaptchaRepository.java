package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class CaptchaRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_TTL = 300; // 5 minutes

    public void store(String token, String text) {
        redisTemplate.opsForValue().set(CAPTCHA_PREFIX + token, text, CAPTCHA_TTL, TimeUnit.SECONDS);
    }

    public String getAndRemove(String token) {
        String key = CAPTCHA_PREFIX + token;
        String text = (String) redisTemplate.opsForValue().getAndDelete(key);
        return text;
    }
}
