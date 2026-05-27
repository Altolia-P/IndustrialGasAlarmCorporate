package com.niit.industrialgasalarmcorporate.application.auth.vo;

import lombok.Data;

@Data
public class UserVO {

    private String userUuid;
    private String username;
    private boolean locked;
}
