package com.niit.industrialgasalarmcorporate.application.alert.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AlertEngineService {

    void evaluate(String deviceUuid, BigDecimal concentration, LocalDateTime timestamp);
}
