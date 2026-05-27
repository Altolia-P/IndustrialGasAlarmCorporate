package com.niit.industrialgasalarmcorporate.application.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResultVO {

    private String token;
    private String userUuid;
    private String username;
    private String role;
}
