package com.niit.collector.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.collector.domain.DeviceDataPoint;
import com.niit.collector.domain.DeviceDataPointRepository;
import com.niit.collector.infrastructure.mapper.DeviceDataPointMapper;
import com.niit.collector.infrastructure.po.DeviceDataPointPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeviceDataPointRepositoryImpl implements DeviceDataPointRepository {

    private final DeviceDataPointMapper deviceDataPointMapper;

    @Override
    public void save(DeviceDataPoint dataPoint) {
        deviceDataPointMapper.insert(toPO(dataPoint));
    }

    @Override
    public List<DeviceDataPoint> findByDeviceUuid(String deviceUuid, LocalDateTime from, LocalDateTime to) {
        LambdaQueryWrapper<DeviceDataPointPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceDataPointPO::getDeviceUuid, deviceUuid)
                .ge(from != null, DeviceDataPointPO::getRecordedAt, from)
                .le(to != null, DeviceDataPointPO::getRecordedAt, to)
                .orderByDesc(DeviceDataPointPO::getRecordedAt);
        return deviceDataPointMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceDataPoint> findToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LambdaQueryWrapper<DeviceDataPointPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(DeviceDataPointPO::getCreatedAt, startOfDay)
                .orderByDesc(DeviceDataPointPO::getCreatedAt);
        return deviceDataPointMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DeviceDataPoint> findLatest(String deviceUuid) {
        LambdaQueryWrapper<DeviceDataPointPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceDataPointPO::getDeviceUuid, deviceUuid)
                .orderByDesc(DeviceDataPointPO::getRecordedAt)
                .last("LIMIT 1");
        DeviceDataPointPO po = deviceDataPointMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    private DeviceDataPoint toDomain(DeviceDataPointPO po) {
        return new DeviceDataPoint(
                po.getDataPointId(),
                po.getDeviceUuid(),
                po.getRecordedAt(),
                po.getConcentration(),
                po.getBattery(),
                po.getTemperature(),
                po.getHumidity(),
                po.getSignalStrength(),
                po.getCreatedAt()
        );
    }

    private DeviceDataPointPO toPO(DeviceDataPoint dp) {
        DeviceDataPointPO po = new DeviceDataPointPO();
        po.setDataPointId(dp.getDataPointId());
        po.setDeviceUuid(dp.getDeviceUuid());
        po.setRecordedAt(dp.getTimestamp());
        po.setConcentration(dp.getConcentration());
        po.setBattery(dp.getBattery());
        po.setTemperature(dp.getTemperature());
        po.setHumidity(dp.getHumidity());
        po.setSignalStrength(dp.getSignalStrength());
        return po;
    }
}
