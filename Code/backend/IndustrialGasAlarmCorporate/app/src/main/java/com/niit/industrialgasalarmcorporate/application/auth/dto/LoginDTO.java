package com.niit.industrialgasalarmcorporate.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不超过50字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 32, message = "密码不超过32字符")
    private String password;

    @Size(max = 10, message = "验证码不超过10字符")
    private String captcha;

    @Size(max = 500, message = "验证码令牌不超过500字符")
    private String captchaToken;
}
