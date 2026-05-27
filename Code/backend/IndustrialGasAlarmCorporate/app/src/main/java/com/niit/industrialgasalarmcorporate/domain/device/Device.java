package com.niit.industrialgasalarmcorporate.domain.device;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Device {

    private final String deviceUuid;
    private final String serialNumber;
    private String name;
    private String model;
    private String customerUuid;
    private String installLocation;
    private LocalDate installDate;
    private GasType gasType;
    private BigDecimal rangeMin;
    private BigDecimal rangeMax;
    private BigDecimal alertThreshold;
    private DeviceStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Device(String serialNumber, String model, String name, String customerUuid,
                  GasType gasType, String installLocation, BigDecimal rangeMin,
                  BigDecimal rangeMax, BigDecimal alertThreshold) {
        this.deviceUuid = UUID.randomUUID().toString();
        this.serialNumber = serialNumber;
        this.model = model;
        this.name = name;
        this.customerUuid = customerUuid;
        this.installLocation = installLocation;
        this.installDate = LocalDate.now();
        this.gasType = gasType;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
        this.alertThreshold = alertThreshold;
        this.status = DeviceStatus.NORMAL;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Device(String deviceUuid, String serialNumber, String name, String model,
                  String customerUuid, String installLocation, LocalDate installDate,
                  GasType gasType, BigDecimal rangeMin, BigDecimal rangeMax,
                  BigDecimal alertThreshold, DeviceStatus status,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.deviceUuid = deviceUuid;
        this.serialNumber = serialNumber;
        this.name = name;
        this.model = model;
        this.customerUuid = customerUuid;
        this.installLocation = installLocation;
        this.installDate = installDate;
        this.gasType = gasType;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
        this.alertThreshold = alertThreshold;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markAbnormal() {
        if (this.status == DeviceStatus.MAINTENANCE) {
            return;
        }
        this.status = DeviceStatus.ABNORMAL;
    }

    public void markNormal() {
        if (this.status == DeviceStatus.MAINTENANCE) {
            return;
        }
        if (this.status != DeviceStatus.ABNORMAL) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅异常状态的设备可恢复为正常");
        }
        this.status = DeviceStatus.NORMAL;
    }

    public void markOffline() {
        if (this.status == DeviceStatus.MAINTENANCE) {
            return;
        }
        this.status = DeviceStatus.OFFLINE;
    }

    public void markOnline() {
        if (this.status == DeviceStatus.MAINTENANCE) {
            return;
        }
        if (this.status != DeviceStatus.OFFLINE) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅离线状态的设备可恢复为在线");
        }
        this.status = DeviceStatus.NORMAL;
    }

    public void startMaintenance() {
        this.status = DeviceStatus.MAINTENANCE;
    }

    public void endMaintenance() {
        if (this.status != DeviceStatus.MAINTENANCE) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅维护中的设备可结束维护");
        }
        this.status = DeviceStatus.NORMAL;
    }

    public void update(String name, String model, String customerUuid, String installLocation,
                       GasType gasType, BigDecimal rangeMin, BigDecimal rangeMax,
                       BigDecimal alertThreshold) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (model != null && !model.isBlank()) {
            this.model = model;
        }
        if (customerUuid != null && !customerUuid.isBlank()) {
            this.customerUuid = customerUuid;
        }
        if (installLocation != null) {
            this.installLocation = installLocation;
        }
        if (gasType != null) {
            this.gasType = gasType;
        }
        if (rangeMin != null) {
            this.rangeMin = rangeMin;
        }
        if (rangeMax != null) {
            this.rangeMax = rangeMax;
        }
        if (alertThreshold != null) {
            this.alertThreshold = alertThreshold;
        }
    }

    public String getDeviceUuid() { return deviceUuid; }
    public String getSerialNumber() { return serialNumber; }
    public String getName() { return name; }
    public String getModel() { return model; }
    public String getCustomerUuid() { return customerUuid; }
    public String getInstallLocation() { return installLocation; }
    public LocalDate getInstallDate() { return installDate; }
    public GasType getGasType() { return gasType; }
    public BigDecimal getRangeMin() { return rangeMin; }
    public BigDecimal getRangeMax() { return rangeMax; }
    public BigDecimal getAlertThreshold() { return alertThreshold; }
    public DeviceStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setStatus(DeviceStatus status) { this.status = status; }
}
