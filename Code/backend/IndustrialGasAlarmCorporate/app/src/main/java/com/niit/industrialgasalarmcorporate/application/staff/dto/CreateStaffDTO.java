package com.niit.industrialgasalarmcorporate.application.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateStaffDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String email;

    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号由4-20位字母、数字或下划线组成")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^.{6,32}$", message = "密码长度6-32位")
    private String password;

    @NotBlank(message = "角色不能为空")
    private String role;

    @NotBlank(message = "状态不能为空")
    private String status;
}
