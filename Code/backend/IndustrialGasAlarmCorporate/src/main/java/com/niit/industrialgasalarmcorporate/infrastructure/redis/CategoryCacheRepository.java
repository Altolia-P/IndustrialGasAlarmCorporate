package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import com.niit.industrialgasalarmcorporate.domain.category.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class CategoryCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "category:";
    private static final long TTL_HOURS = 1;

    @SuppressWarnings("unchecked")
    public List<Category> get(String type) {
        Object value = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + type);
        if (value instanceof List) {
            return (List<Category>) value;
        }
        return null;
    }

    public void put(String type, List<Category> categories) {
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + type, categories, TTL_HOURS, TimeUnit.HOURS);
    }

    public void evict(String type) {
        redisTemplate.delete(CACHE_KEY_PREFIX + type);
    }
}
