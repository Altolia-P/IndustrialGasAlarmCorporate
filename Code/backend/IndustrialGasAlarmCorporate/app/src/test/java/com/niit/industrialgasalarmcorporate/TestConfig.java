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

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.AIChatRateLimitRepository aiChatRateLimitRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.AIChatRateLimitRepository.class);
    }

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataWindowRepository deviceDataWindowRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataWindowRepository.class);
    }

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.AlertSuppressRepository alertSuppressRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.AlertSuppressRepository.class);
    }

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.ChatSessionRepository chatSessionRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.ChatSessionRepository.class);
    }

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository dashboardCacheRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository.class);
    }

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.LoginRateLimitRepository loginRateLimitRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.LoginRateLimitRepository.class);
    }

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.RegisterRateLimitRepository registerRateLimitRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.RegisterRateLimitRepository.class);
    }

    @Bean
    public com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataRateLimitRepository deviceDataRateLimitRepository() {
        return mock(com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataRateLimitRepository.class);
    }
}
