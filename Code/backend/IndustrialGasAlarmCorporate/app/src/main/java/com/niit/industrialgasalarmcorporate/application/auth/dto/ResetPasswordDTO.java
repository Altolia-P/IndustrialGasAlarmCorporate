package com.niit.industrialgasalarmcorporate.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度 8-32 位")
    private String newPassword;

    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;
}
