package com.niit.industrialgasalarmcorporate.application.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaVO {

    private String image;
    private String token;
}
