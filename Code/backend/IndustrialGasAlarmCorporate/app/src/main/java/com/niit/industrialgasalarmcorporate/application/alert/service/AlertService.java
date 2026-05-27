package com.niit.industrialgasalarmcorporate.application.alert.service;

import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface AlertService {

    Page<AlertVO> findAlerts(String deviceUuid, String alertType, String severity,
                             String status, int page, int size);

    AlertVO getAlert(String alertUuid);

    void confirmAlert(String alertUuid, String confirmedBy);

    void resolveAlert(String alertUuid, String resolvedBy);

    void closeAlert(String alertUuid);
}
