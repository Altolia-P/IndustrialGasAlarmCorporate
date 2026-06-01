package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.domain.device.GasType;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.DeviceMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.DevicePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepository {

    private final DeviceMapper deviceMapper;

    @Override
    public Optional<Device> findById(String deviceUuid) {
        DevicePO po = deviceMapper.selectById(deviceUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public List<Device> findByIds(Collection<String> deviceUuids) {
        if (deviceUuids == null || deviceUuids.isEmpty()) return Collections.emptyList();
        return deviceMapper.selectBatchIds(deviceUuids).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Device> findBySerialNumber(String serialNumber) {
        LambdaQueryWrapper<DevicePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DevicePO::getSerialNumber, serialNumber);
        DevicePO po = deviceMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public void save(Device device) {
        DevicePO po = toPO(device);
        DevicePO existing = deviceMapper.selectById(device.getDeviceUuid());
        if (existing != null) {
            deviceMapper.updateById(po);
        } else {
            deviceMapper.insert(po);
        }
    }

    @Override
    public void deleteById(String deviceUuid) {
        deviceMapper.deleteById(deviceUuid);
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<Device> findAllWithFilter(
            String customerUuid, String model, String gasType,
            String status, int page, int size) {
        LambdaQueryWrapper<DevicePO> wrapper = new LambdaQueryWrapper<>();
        if (customerUuid != null && !customerUuid.isBlank()) {
            wrapper.eq(DevicePO::getCustomerUuid, customerUuid);
        }
        if (model != null && !model.isBlank()) {
            wrapper.like(DevicePO::getModel, model);
        }
        if (gasType != null && !gasType.isBlank()) {
            wrapper.eq(DevicePO::getGasType, gasType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(DevicePO::getStatus, status);
        }
        wrapper.orderByDesc(DevicePO::getCreatedAt);
        Page<DevicePO> mpPage = new Page<>(page, size);
        Page<DevicePO> result = deviceMapper.selectPage(mpPage, wrapper);
        List<Device> devices = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                devices, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public List<Device> findByCustomerUuid(String customerUuid) {
        LambdaQueryWrapper<DevicePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DevicePO::getCustomerUuid, customerUuid)
                .orderByDesc(DevicePO::getCreatedAt);
        return deviceMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Device> findAllOnline() {
        LambdaQueryWrapper<DevicePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DevicePO::getStatus, DeviceStatus.NORMAL.name(), DeviceStatus.ABNORMAL.name())
                .orderByDesc(DevicePO::getCreatedAt);
        return deviceMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByStatus(DeviceStatus status) {
        LambdaQueryWrapper<DevicePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DevicePO::getStatus, status.name());
        return deviceMapper.selectCount(wrapper);
    }

    private Device toDomain(DevicePO po) {
        return new Device(
                po.getDeviceUuid(),
                po.getSerialNumber(),
                po.getName(),
                po.getModel(),
                po.getCustomerUuid(),
                po.getInstallLocation(),
                po.getInstallDate(),
                GasType.valueOf(po.getGasType()),
                po.getRangeMin(),
                po.getRangeMax(),
                po.getAlertThreshold(),
                DeviceStatus.valueOf(po.getStatus()),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    private DevicePO toPO(Device device) {
        DevicePO po = new DevicePO();
        po.setDeviceUuid(device.getDeviceUuid());
        po.setSerialNumber(device.getSerialNumber());
        po.setName(device.getName());
        po.setModel(device.getModel());
        po.setCustomerUuid(device.getCustomerUuid());
        po.setInstallLocation(device.getInstallLocation());
        po.setInstallDate(device.getInstallDate());
        po.setGasType(device.getGasType().name());
        po.setRangeMin(device.getRangeMin());
        po.setRangeMax(device.getRangeMax());
        po.setAlertThreshold(device.getAlertThreshold());
        po.setStatus(device.getStatus().name());
        return po;
    }
}
