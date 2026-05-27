package com.niit.industrialgasalarmcorporate.domain.event;

import com.niit.industrialgasalarmcorporate.domain.shared.DomainEvent;

public class AlertCreatedEvent extends DomainEvent {

    private final String alertUuid;
    private final String deviceUuid;
    private final String alertType;
    private final String severity;
    private final String message;

    public AlertCreatedEvent(String alertUuid, String deviceUuid, String alertType,
                             String severity, String message) {
        super();
        this.alertUuid = alertUuid;
        this.deviceUuid = deviceUuid;
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
    }

    public String getAlertUuid() { return alertUuid; }
    public String getDeviceUuid() { return deviceUuid; }
    public String getAlertType() { return alertType; }
    public String getSeverity() { return severity; }
    public String getMessage() { return message; }
}
