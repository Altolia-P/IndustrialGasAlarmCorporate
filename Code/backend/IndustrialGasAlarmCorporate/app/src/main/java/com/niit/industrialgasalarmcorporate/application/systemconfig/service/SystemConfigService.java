package com.niit.industrialgasalarmcorporate.application.systemconfig.service;

import com.niit.industrialgasalarmcorporate.application.systemconfig.vo.SystemConfigVO;

import java.util.List;

public interface SystemConfigService {

    SystemConfigVO getByKey(String configKey);

    List<SystemConfigVO> listAll();

    SystemConfigVO update(String configKey, String configValue, String description);

    SystemConfigVO create(String configKey, String configValue, String description);

    void delete(String configKey);
}
