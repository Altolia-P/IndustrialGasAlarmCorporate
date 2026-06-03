package com.niit.industrialgasalarmcorporate.application.device.service.impl;

import com.niit.industrialgasalarmcorporate.application.device.dto.CreateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.dto.UpdateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.domain.device.GasType;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceService 设备管理")
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardCacheRepository dashboardCacheRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private Device device;
    private static final String DEVICE_UUID = "device-001";

    @BeforeEach
    void setUp() {
        device = new Device(DEVICE_UUID, "SN-001", "test-api-token", "测试设备", "GT-M4", "cust-001",
                "A区", null, GasType.CH4, new BigDecimal("0"), new BigDecimal("5.0"),
                new BigDecimal("1.0"), DeviceStatus.NORMAL,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("CRUD 操作")
    class CrudOperations {

        @Test
        @DisplayName("根据 UUID 查询设备")
        void shouldGetDeviceByUuid() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));

            DeviceVO vo = deviceService.getDevice(DEVICE_UUID);

            assertNotNull(vo);
            assertEquals("SN-001", vo.getSerialNumber());
            assertEquals("测试设备", vo.getName());
        }

        @Test
        @DisplayName("查询不存在的设备抛出 BusinessException")
        void shouldThrowWhenDeviceNotFound() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> deviceService.getDevice(DEVICE_UUID));
            assertEquals(ErrorCode.DEVICE_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("创建设备")
        void shouldCreateDevice() {
            CreateDeviceDTO dto = new CreateDeviceDTO();
            dto.setSerialNumber("SN-NEW");
            dto.setName("新设备");
            dto.setModel("GT-M4");
            dto.setCustomerUuid("cust-001");
            dto.setGasType("CH4");
            dto.setRangeMin(new BigDecimal("0"));
            dto.setRangeMax(new BigDecimal("5.0"));
            dto.setAlertThreshold(new BigDecimal("1.0"));

            when(deviceRepository.findBySerialNumber("SN-NEW")).thenReturn(Optional.empty());

            DeviceVO vo = deviceService.createDevice(dto);

            assertNotNull(vo);
            assertEquals("SN-NEW", vo.getSerialNumber());
            verify(deviceRepository).save(any(Device.class));
        }

        @Test
        @DisplayName("序列号重复时创建失败")
        void shouldFailWhenSerialNumberDuplicate() {
            CreateDeviceDTO dto = new CreateDeviceDTO();
            dto.setSerialNumber("SN-001");
            dto.setName("新设备");
            dto.setModel("GT-M4");
            dto.setCustomerUuid("cust-001");
            dto.setGasType("CH4");

            when(deviceRepository.findBySerialNumber("SN-001")).thenReturn(Optional.of(device));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> deviceService.createDevice(dto));
            assertEquals(ErrorCode.DEVICE_SERIAL_DUPLICATE.getCode(), ex.getCode());
            verify(deviceRepository, never()).save(any());
        }

        @Test
        @DisplayName("更新设备")
        void shouldUpdateDevice() {
            UpdateDeviceDTO dto = new UpdateDeviceDTO();
            dto.setName("更新后的名称");
            dto.setInstallLocation("新位置");

            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));

            DeviceVO vo = deviceService.updateDevice(DEVICE_UUID, dto);

            assertNotNull(vo);
            verify(deviceRepository).save(any(Device.class));
        }

        @Test
        @DisplayName("删除设备")
        void shouldDeleteDevice() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));

            deviceService.deleteDevice(DEVICE_UUID);

            verify(deviceRepository).deleteById(DEVICE_UUID);
        }

        @Test
        @DisplayName("删除不存在的设备抛出异常")
        void shouldThrowWhenDeleteNotFound() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> deviceService.deleteDevice(DEVICE_UUID));
            verify(deviceRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("状态转换")
    class StatusTransitions {

        @Test
        @DisplayName("标记为异常")
        void shouldMarkAbnormal() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));

            deviceService.markAbnormal(DEVICE_UUID);

            ArgumentCaptor<Device> captor = ArgumentCaptor.forClass(Device.class);
            verify(deviceRepository).save(captor.capture());
            assertEquals(DeviceStatus.ABNORMAL, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("标记为正常")
        void shouldMarkNormal() {
            Device abnormal = new Device(DEVICE_UUID, "SN-001", "test-api-token", "测试设备", "GT-M4", "cust-001",
                    "A区", null, GasType.CH4, new BigDecimal("0"), new BigDecimal("5.0"),
                    new BigDecimal("1.0"), DeviceStatus.ABNORMAL,
                    LocalDateTime.now(), LocalDateTime.now());
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(abnormal));

            deviceService.markNormal(DEVICE_UUID);

            ArgumentCaptor<Device> captor = ArgumentCaptor.forClass(Device.class);
            verify(deviceRepository).save(captor.capture());
            assertEquals(DeviceStatus.NORMAL, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("标记为离线")
        void shouldMarkOffline() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));

            deviceService.markOffline(DEVICE_UUID);

            ArgumentCaptor<Device> captor = ArgumentCaptor.forClass(Device.class);
            verify(deviceRepository).save(captor.capture());
            assertEquals(DeviceStatus.OFFLINE, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("进入维护模式")
        void shouldStartMaintenance() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));

            deviceService.startMaintenance(DEVICE_UUID);

            ArgumentCaptor<Device> captor = ArgumentCaptor.forClass(Device.class);
            verify(deviceRepository).save(captor.capture());
            assertEquals(DeviceStatus.MAINTENANCE, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("结束维护")
        void shouldEndMaintenance() {
            Device maintenance = new Device(DEVICE_UUID, "SN-001", "test-api-token", "测试设备", "GT-M4", "cust-001",
                    "A区", null, GasType.CH4, new BigDecimal("0"), new BigDecimal("5.0"),
                    new BigDecimal("1.0"), DeviceStatus.MAINTENANCE,
                    LocalDateTime.now(), LocalDateTime.now());
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(maintenance));

            deviceService.endMaintenance(DEVICE_UUID);

            ArgumentCaptor<Device> captor = ArgumentCaptor.forClass(Device.class);
            verify(deviceRepository).save(captor.capture());
            assertEquals(DeviceStatus.NORMAL, captor.getValue().getStatus());
        }
    }
}
