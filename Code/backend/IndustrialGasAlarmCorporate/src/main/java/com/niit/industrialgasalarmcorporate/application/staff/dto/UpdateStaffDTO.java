package com.niit.industrialgasalarmcorporate.application.staff.dto;

import lombok.Data;

@Data
public class UpdateStaffDTO {

    private String name;

    private String phone;

    private String email;

    private String role;

    private String status;
}
