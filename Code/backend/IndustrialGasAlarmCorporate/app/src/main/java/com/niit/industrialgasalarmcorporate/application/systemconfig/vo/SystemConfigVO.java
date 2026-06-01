package com.niit.industrialgasalarmcorporate.application.systemconfig.vo;

import lombok.Data;

@Data
public class SystemConfigVO {

    private String configKey;
    private String configValue;
    private String description;
    private String updatedAt;
}
