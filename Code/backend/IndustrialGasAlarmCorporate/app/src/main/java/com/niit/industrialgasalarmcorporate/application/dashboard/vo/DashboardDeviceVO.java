package com.niit.industrialgasalarmcorporate.application.dashboard.vo;

public class DashboardDeviceVO {

    private final String deviceUuid;
    private final String name;
    private final String model;
    private final String gasType;
    private final String installLocation;
    private final String status;
    private final String latestConcentration;
    private final String customerUuid;
    private final String customerName;

    public DashboardDeviceVO(String deviceUuid, String name, String model, String gasType,
                              String installLocation, String status, String latestConcentration,
                              String customerUuid, String customerName) {
        this.deviceUuid = deviceUuid;
        this.name = name;
        this.model = model;
        this.gasType = gasType;
        this.installLocation = installLocation;
        this.status = status;
        this.latestConcentration = latestConcentration;
        this.customerUuid = customerUuid;
        this.customerName = customerName;
    }

    public String getDeviceUuid() { return deviceUuid; }
    public String getName() { return name; }
    public String getModel() { return model; }
    public String getGasType() { return gasType; }
    public String getInstallLocation() { return installLocation; }
    public String getStatus() { return status; }
    public String getLatestConcentration() { return latestConcentration; }
    public String getCustomerUuid() { return customerUuid; }
    public String getCustomerName() { return customerName; }
}
