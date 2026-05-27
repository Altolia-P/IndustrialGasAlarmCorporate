package com.niit.industrialgasalarmcorporate.application.alert.service;

import com.niit.industrialgasalarmcorporate.domain.device.DeviceDataPoint;

public interface AlertEngineService {

    void evaluate(DeviceDataPoint dataPoint);
}
