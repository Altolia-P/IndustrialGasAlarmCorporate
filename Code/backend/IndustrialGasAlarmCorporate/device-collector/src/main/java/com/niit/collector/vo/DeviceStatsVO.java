package com.niit.collector.vo;

public class DeviceStatsVO {

    private final long todayDataPoints;
    private final String avgConcentration;

    public DeviceStatsVO(long todayDataPoints, String avgConcentration) {
        this.todayDataPoints = todayDataPoints;
        this.avgConcentration = avgConcentration;
    }

    public long getTodayDataPoints() { return todayDataPoints; }
    public String getAvgConcentration() { return avgConcentration; }
}
