package com.niit.industrialgasalarmcorporate.domain.device;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceDataPointRepository {

    void save(DeviceDataPoint dataPoint);

    List<DeviceDataPoint> findByDeviceUuid(String deviceUuid, LocalDateTime from, LocalDateTime to);

    Optional<DeviceDataPoint> findLatest(String deviceUuid);
}
