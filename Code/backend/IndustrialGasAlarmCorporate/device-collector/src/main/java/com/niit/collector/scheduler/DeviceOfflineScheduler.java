package com.niit.collector.scheduler;

import com.niit.collector.mq.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceOfflineScheduler {

    private static final String LAST_SEEN_KEY = "device:last_seen";
    private static final long OFFLINE_THRESHOLD_SECONDS = 60;

    private final RedissonClient redissonClient;
    private final EventPublisher eventPublisher;

    @Scheduled(fixedDelay = 15_000)
    public void detectOfflineDevices() {
        RScoredSortedSet<String> lastSeen = redissonClient.getScoredSortedSet(LAST_SEEN_KEY);
        long cutoff = Instant.now().getEpochSecond() - OFFLINE_THRESHOLD_SECONDS;

        var staleEntries = lastSeen.entryRange(0, true, cutoff, true);
        if (staleEntries == null || staleEntries.isEmpty()) {
            return;
        }

        for (var entry : staleEntries) {
            String deviceUuid = entry.getValue();
            try {
                eventPublisher.publishDeviceOffline(deviceUuid);
                lastSeen.remove(deviceUuid);
                log.info("设备离线消息已发送: deviceUuid={}", deviceUuid);
            } catch (Exception e) {
                log.warn("发送设备离线消息失败: deviceUuid={}, error={}", deviceUuid, e.getMessage());
            }
        }
    }
}
