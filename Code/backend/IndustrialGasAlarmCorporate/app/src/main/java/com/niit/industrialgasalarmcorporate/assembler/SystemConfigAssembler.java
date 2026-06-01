package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.systemconfig.vo.SystemConfigVO;
import com.niit.industrialgasalarmcorporate.domain.systemconfig.SystemConfig;

import java.time.format.DateTimeFormatter;

public final class SystemConfigAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private SystemConfigAssembler() {}

    public static SystemConfigVO toVO(SystemConfig config) {
        SystemConfigVO vo = new SystemConfigVO();
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setDescription(config.getDescription());
        vo.setUpdatedAt(config.getUpdatedAt() != null ? config.getUpdatedAt().format(DTF) : null);
        return vo;
    }
}
