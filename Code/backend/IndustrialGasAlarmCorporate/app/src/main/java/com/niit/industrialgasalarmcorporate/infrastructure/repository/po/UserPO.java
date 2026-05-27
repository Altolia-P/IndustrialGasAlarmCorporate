package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_admin_user")
public class UserPO {

    @TableId
    private String userUuid;

    private String username;

    private String passwordHash;

    private String phone;

    private String company;

    private Integer failCount;

    private Integer locked;

    private LocalDateTime lockTime;

    private LocalDateTime lastLoginAt;

    private String role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
