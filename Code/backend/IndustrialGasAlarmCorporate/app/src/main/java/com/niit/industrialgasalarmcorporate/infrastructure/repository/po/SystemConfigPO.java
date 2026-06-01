package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_system_config")
public class SystemConfigPO {

    @TableId
    private String configKey;

    private String configValue;

    private String description;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
