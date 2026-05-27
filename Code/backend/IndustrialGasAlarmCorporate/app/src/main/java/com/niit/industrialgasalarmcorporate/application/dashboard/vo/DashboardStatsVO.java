package com.niit.industrialgasalarmcorporate.application.dashboard.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardStatsVO {

    private int totalDevices;
    private int normalDevices;
    private int abnormalDevices;
    private int offlineDevices;
    private int maintenanceDevices;

    private int totalAlerts;
    private int alertsToday;
    private int pendingAlerts;
    private int criticalAlerts;
    private int warningAlerts;

    private List<AlertTrendItem> alertTrend;
}
