package com.niit.collector.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_device_data_point (
                        data_point_id   CHAR(36)     PRIMARY KEY,
                        device_uuid     CHAR(36)     NOT NULL,
                        recorded_at     DATETIME     NOT NULL,
                        concentration   DECIMAL(10,4) NOT NULL,
                        battery         DECIMAL(10,4) NULL,
                        temperature     DECIMAL(10,4) NULL,
                        humidity        DECIMAL(10,4) NULL,
                        signal_strength INT          NULL DEFAULT 0,
                        created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                    """);
            log.info("device-collector 数据库初始化完成");
        } catch (Exception e) {
            log.warn("device-collector 数据库初始化失败（DB 不可用）: {}", e.getMessage());
        }
    }
}
