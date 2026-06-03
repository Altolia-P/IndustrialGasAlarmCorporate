package com.niit.industrialgasalarmcorporate.application.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitMessageDTO {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不超过50字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "需求描述不能为空")
    @Size(min = 5, max = 500, message = "需求描述5-500字符")
    private String content;
}
