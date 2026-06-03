package com.niit.industrialgasalarmcorporate.application.dashboard.vo;

public class DashboardAlertVO {

    private final String alertUuid;
    private final String deviceUuid;
    private final String deviceName;
    private final String severity;
    private final String alertType;
    private final String concentration;
    private final String message;
    private final String triggeredAt;

    public DashboardAlertVO(String alertUuid, String deviceUuid, String deviceName,
                             String severity, String alertType, String concentration,
                             String message, String triggeredAt) {
        this.alertUuid = alertUuid;
        this.deviceUuid = deviceUuid;
        this.deviceName = deviceName;
        this.severity = severity;
        this.alertType = alertType;
        this.concentration = concentration;
        this.message = message;
        this.triggeredAt = triggeredAt;
    }

    public String getAlertUuid() { return alertUuid; }
    public String getDeviceUuid() { return deviceUuid; }
    public String getDeviceName() { return deviceName; }
    public String getSeverity() { return severity; }
    public String getAlertType() { return alertType; }
    public String getConcentration() { return concentration; }
    public String getMessage() { return message; }
    public String getTriggeredAt() { return triggeredAt; }
}
