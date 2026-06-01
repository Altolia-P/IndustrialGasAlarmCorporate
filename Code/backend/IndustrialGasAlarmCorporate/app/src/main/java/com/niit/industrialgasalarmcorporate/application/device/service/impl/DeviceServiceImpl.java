package com.niit.industrialgasalarmcorporate.application.device.service.impl;

import com.niit.industrialgasalarmcorporate.application.device.dto.CreateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.dto.UpdateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.service.DeviceService;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceVO;
import com.niit.industrialgasalarmcorporate.assembler.DeviceAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceVO> findDevices(String customerUuid, String model, String gasType,
                                      String status, int page, int size) {
        Page<Device> domainPage = deviceRepository.findAllWithFilter(
                customerUuid, model, gasType, status, page, size);
        return new Page<>(
                domainPage.getContent().stream()
                        .map(d -> enrich(DeviceAssembler.toVO(d)))
                        .collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceVO getDevice(String deviceUuid) {
        Device device = deviceRepository.findById(deviceUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        return enrich(DeviceAssembler.toVO(device));
    }

    @Override
    @Transactional
    public DeviceVO createDevice(CreateDeviceDTO dto) {
        if (deviceRepository.findBySerialNumber(dto.getSerialNumber()).isPresent()) {
            throw new BusinessException(ErrorCode.DEVICE_SERIAL_DUPLICATE);
        }
        Device device = DeviceAssembler.toEntity(dto);
        deviceRepository.save(device);
        return enrich(DeviceAssembler.toVO(device));
    }

    @Override
    @Transactional
    public DeviceVO updateDevice(String deviceUuid, UpdateDeviceDTO dto) {
        Device device = deviceRepository.findById(deviceUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        DeviceAssembler.updateEntity(device, dto);
        deviceRepository.save(device);
        return enrich(DeviceAssembler.toVO(device));
    }

    @Override
    @Transactional
    public void deleteDevice(String deviceUuid) {
        if (deviceRepository.findById(deviceUuid).isEmpty()) {
            throw new BusinessException(ErrorCode.DEVICE_NOT_FOUND);
        }
        deviceRepository.deleteById(deviceUuid);
    }

    @Override
    @Transactional
    public void markAbnormal(String deviceUuid) {
        Device device = deviceRepository.findById(deviceUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        device.markAbnormal();
        deviceRepository.save(device);
    }

    @Override
    @Transactional
    public void markNormal(String deviceUuid) {
        Device device = deviceRepository.findById(deviceUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        device.markNormal();
        deviceRepository.save(device);
    }

    @Override
    @Transactional
    public void markOffline(String deviceUuid) {
        Device device = deviceRepository.findById(deviceUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        device.markOffline();
        deviceRepository.save(device);
    }

    @Override
    @Transactional
    public void startMaintenance(String deviceUuid) {
        Device device = deviceRepository.findById(deviceUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        device.startMaintenance();
        deviceRepository.save(device);
    }

    @Override
    @Transactional
    public void endMaintenance(String deviceUuid) {
        Device device = deviceRepository.findById(deviceUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        device.endMaintenance();
        deviceRepository.save(device);
    }

    private DeviceVO enrich(DeviceVO vo) {
        String customerUuid = vo.getCustomerUuid();
        if (customerUuid != null && !customerUuid.isBlank()) {
            userRepository.findById(customerUuid).ifPresent(user -> {
                vo.setCustomerName(user.getCompany());
                vo.setCustomerPhone(user.getPhone());
            });
        }
        return vo;
    }
}
