package com.niit.industrialgasalarmcorporate;

import com.niit.industrialgasalarmcorporate.infrastructure.redis.CaptchaRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.CategoryCacheRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.JwtBlacklistRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.MessageRateLimitRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public MessageRateLimitRepository messageRateLimitRepository() {
        return mock(MessageRateLimitRepository.class);
    }

    @Bean
    public CategoryCacheRepository categoryCacheRepository() {
        return mock(CategoryCacheRepository.class);
    }

    @Bean
    public JwtBlacklistRepository jwtBlacklistRepository() {
        return mock(JwtBlacklistRepository.class);
    }

    @Bean
    public CaptchaRepository captchaRepository() {
        return mock(CaptchaRepository.class);
    }
}
