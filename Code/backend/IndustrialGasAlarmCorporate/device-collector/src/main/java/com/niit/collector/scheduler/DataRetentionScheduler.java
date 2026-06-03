package com.niit.collector.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataRetentionScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final int retentionDays;

    public DataRetentionScheduler(JdbcTemplate jdbcTemplate,
                                  @Value("${collector.retention-days:30}") int retentionDays) {
        this.jdbcTemplate = jdbcTemplate;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void purgeOldData() {
        try {
            int deleted = jdbcTemplate.update(
                    "DELETE FROM t_device_data_point WHERE recorded_at < DATE_SUB(NOW(), INTERVAL ? DAY)",
                    retentionDays);
            if (deleted > 0) {
                log.info("数据保留清理完成: 删除 {} 条超过 {} 天的旧数据", deleted, retentionDays);
            }
        } catch (Exception e) {
            log.warn("数据保留清理失败: {}", e.getMessage());
        }
    }
}
