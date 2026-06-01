package com.niit.industrialgasalarmcorporate.application.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateStaffProfileDTO {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不超过50字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 100, message = "邮箱不超过100字符")
    private String email;
}
