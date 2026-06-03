package com.niit.industrialgasalarmcorporate.application.staff.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateStaffDTO {

    @Size(max = 100, message = "姓名不超过100字符")
    private String name;

    @Size(max = 20, message = "电话不超过20字符")
    private String phone;

    @Size(max = 100, message = "邮箱不超过100字符")
    private String email;

    @Size(max = 50, message = "角色不超过50字符")
    private String role;

    @Size(max = 20, message = "状态不超过20字符")
    private String status;
}
