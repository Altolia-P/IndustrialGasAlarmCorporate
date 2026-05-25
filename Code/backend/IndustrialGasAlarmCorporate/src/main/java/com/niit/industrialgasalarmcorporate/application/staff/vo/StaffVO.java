package com.niit.industrialgasalarmcorporate.application.staff.vo;

import lombok.Data;

@Data
public class StaffVO {

    private String staffUuid;
    private String name;
    private String phone;
    private String email;
    private String role;
    private String status;
    private String createdAt;
}
