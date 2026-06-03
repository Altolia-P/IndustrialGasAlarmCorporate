package com.niit.industrialgasalarmcorporate.application.dashboard.vo;

public class DashboardOverviewVO {

    private final int onlineCount;
    private final int totalCount;
    private final int alertCount;
    private final long todayDataPoints;
    private final String avgConcentration;
    private final String uptime;

    public DashboardOverviewVO(int onlineCount, int totalCount, int alertCount,
                                long todayDataPoints, String avgConcentration, String uptime) {
        this.onlineCount = onlineCount;
        this.totalCount = totalCount;
        this.alertCount = alertCount;
        this.todayDataPoints = todayDataPoints;
        this.avgConcentration = avgConcentration;
        this.uptime = uptime;
    }

    public int getOnlineCount() { return onlineCount; }
    public int getTotalCount() { return totalCount; }
    public int getAlertCount() { return alertCount; }
    public long getTodayDataPoints() { return todayDataPoints; }
    public String getAvgConcentration() { return avgConcentration; }
    public String getUptime() { return uptime; }
}
