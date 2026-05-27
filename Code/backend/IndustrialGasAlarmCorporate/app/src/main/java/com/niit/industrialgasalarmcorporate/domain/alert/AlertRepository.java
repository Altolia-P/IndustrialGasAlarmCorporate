package com.niit.industrialgasalarmcorporate.domain.alert;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface AlertRepository {

    Optional<Alert> findById(String alertUuid);

    Page<Alert> findByDeviceUuid(String deviceUuid, int page, int size);

    Page<Alert> findAllWithFilter(String deviceUuid, String alertType, String severity,
                                  String status, int page, int size);

    void save(Alert alert);

    long countPendingByDevice(String deviceUuid);

    long countByStatus(AlertStatus status);

    long countBySeverity(AlertSeverity severity);

    long countToday();

    Map<LocalDate, Long> countByDay(LocalDate from, LocalDate to);
}
