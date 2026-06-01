package com.niit.industrialgasalarmcorporate.domain.device;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository {

    Optional<Device> findById(String deviceUuid);

    Optional<Device> findBySerialNumber(String serialNumber);

    void save(Device device);

    void deleteById(String deviceUuid);

    Page<Device> findAllWithFilter(String customerUuid, String model, String gasType,
                                    String status, int page, int size);

    List<Device> findByIds(Collection<String> deviceUuids);

    List<Device> findByCustomerUuid(String customerUuid);

    List<Device> findAllOnline();

    long countByStatus(DeviceStatus status);
}
