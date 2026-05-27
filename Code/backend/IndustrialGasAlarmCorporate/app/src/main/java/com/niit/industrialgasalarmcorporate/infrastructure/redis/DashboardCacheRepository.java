package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardCacheRepository {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String CACHE_KEY = "dashboard:stats";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public <T> Optional<T> get(String key, Class<T> clazz) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, clazz));
        } catch (JsonProcessingException e) {
            log.warn("Dashboard缓存反序列化失败: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void set(String key, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (JsonProcessingException e) {
            log.warn("Dashboard缓存序列化失败: {}", e.getMessage());
        }
    }

    public void evict(String key) {
        stringRedisTemplate.delete(key);
    }

    public Optional<String> getStats() {
        String json = stringRedisTemplate.opsForValue().get(CACHE_KEY);
        return Optional.ofNullable(json);
    }

    public void setStats(String json) {
        stringRedisTemplate.opsForValue().set(CACHE_KEY, json, CACHE_TTL);
    }
}
