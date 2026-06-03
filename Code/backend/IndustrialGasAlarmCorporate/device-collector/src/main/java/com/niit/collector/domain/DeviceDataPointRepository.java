package com.niit.collector.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeviceDataPointRepository {

    void save(DeviceDataPoint dataPoint);

    List<DeviceDataPoint> findByDeviceUuid(String deviceUuid, LocalDateTime from, LocalDateTime to);

    Optional<DeviceDataPoint> findLatest(String deviceUuid);

    List<DeviceDataPoint> findToday();

    List<DeviceDataPoint> findTodayByDeviceUuids(Collection<String> deviceUuids);
}
