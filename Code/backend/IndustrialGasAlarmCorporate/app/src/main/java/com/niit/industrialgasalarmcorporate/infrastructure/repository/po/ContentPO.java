package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_content")
public class ContentPO {

    @TableId
    private String contentUuid;

    private String title;

    private String summary;

    private String body;

    private String coverImage;

    private String type;

    private String status;

    private String categoryUuid;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
