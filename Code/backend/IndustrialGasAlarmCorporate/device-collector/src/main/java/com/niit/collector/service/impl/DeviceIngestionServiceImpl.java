package com.niit.collector.service.impl;

import com.niit.collector.assembler.DeviceDataPointAssembler;
import com.niit.collector.domain.DeviceDataPoint;
import com.niit.collector.domain.DeviceDataPointRepository;
import com.niit.collector.dto.DeviceDataPointDTO;
import com.niit.collector.mq.EventPublisher;
import com.niit.collector.service.DeviceIngestionService;
import com.niit.collector.vo.DeviceDataPointVO;
import com.niit.collector.vo.DeviceStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceIngestionServiceImpl implements DeviceIngestionService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LAST_SEEN_KEY = "device:last_seen";

    private final DeviceDataPointRepository deviceDataPointRepository;
    private final TransactionTemplate transactionTemplate;
    private final RedissonClient redissonClient;
    private final EventPublisher eventPublisher;

    @Override
    public void ingest(DeviceDataPointDTO dto) {
        String lockKey = "device:ingest:" + dto.getDeviceUuid();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(5000, 3000, TimeUnit.MILLISECONDS)) {
                try {
                    DeviceDataPoint dataPoint = DeviceDataPointAssembler.toEntity(dto);

                    transactionTemplate.execute(status -> {
                        deviceDataPointRepository.save(dataPoint);
                        return dataPoint;
                    });

                    RScoredSortedSet<String> lastSeen = redissonClient.getScoredSortedSet(LAST_SEEN_KEY);
                    lastSeen.add(Instant.now().getEpochSecond(), dto.getDeviceUuid());

                    // 异步：通过 RabbitMQ 通知主应用标记上线 + 评估告警
                    eventPublisher.publishDeviceOnline(dto.getDeviceUuid());
                    eventPublisher.publishAlertEvaluate(
                            dataPoint.getDeviceUuid(),
                            dataPoint.getConcentration().toPlainString(),
                            dataPoint.getTimestamp().toString());
                } catch (Exception e) {
                    log.warn("数据录入失败（DB/Redis/MQ 不可用），丢弃该数据点: deviceUuid={}, error={}",
                            dto.getDeviceUuid(), e.getMessage());
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("获取分布式锁超时: deviceUuid={}", dto.getDeviceUuid());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("获取分布式锁被中断: deviceUuid={}", dto.getDeviceUuid());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceDataPointVO> getDataPoints(String deviceUuid, String from, String to) {
        LocalDateTime fromTime = from != null ? LocalDateTime.parse(from, DTF) : null;
        LocalDateTime toTime = to != null ? LocalDateTime.parse(to, DTF) : null;
        return deviceDataPointRepository.findByDeviceUuid(deviceUuid, fromTime, toTime).stream()
                .map(DeviceDataPointAssembler::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceDataPointVO getLatest(String deviceUuid) {
        return deviceDataPointRepository.findLatest(deviceUuid)
                .map(DeviceDataPointAssembler::toVO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceStatsVO getStats() {
        List<DeviceDataPoint> today = deviceDataPointRepository.findToday();
        long count = today.size();
        String avg = "—";
        if (count > 0) {
            double sum = today.stream()
                    .mapToDouble(dp -> dp.getConcentration() != null ? dp.getConcentration().doubleValue() : 0)
                    .sum();
            avg = String.format("%.2f", sum / count);
        }
        return new DeviceStatsVO(count, avg);
    }
}
