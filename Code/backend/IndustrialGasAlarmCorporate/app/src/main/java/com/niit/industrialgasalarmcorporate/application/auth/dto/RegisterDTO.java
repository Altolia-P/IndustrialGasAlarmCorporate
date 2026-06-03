package com.niit.industrialgasalarmcorporate.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不超过50字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度为8-32位")
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 20, message = "手机号不超过20字符")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 200, message = "公司名不超过200字符")
    private String company;
}
