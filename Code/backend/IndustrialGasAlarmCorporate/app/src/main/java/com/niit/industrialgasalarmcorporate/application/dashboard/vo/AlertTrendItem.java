package com.niit.industrialgasalarmcorporate.application.dashboard.vo;

import lombok.Data;

@Data
public class AlertTrendItem {

    private String date;
    private long count;

    public AlertTrendItem() {}

    public AlertTrendItem(String date, long count) {
        this.date = date;
        this.count = count;
    }
}
