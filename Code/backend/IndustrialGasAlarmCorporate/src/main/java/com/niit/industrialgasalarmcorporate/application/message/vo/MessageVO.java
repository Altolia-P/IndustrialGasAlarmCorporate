package com.niit.industrialgasalarmcorporate.application.message.vo;

import lombok.Data;

@Data
public class MessageVO {

    private String messageUuid;
    private String name;
    private String phone;
    private String content;
    private String status;
    private String processor;
    private String remark;
    private String submittedAt;
    private String processedAt;
}
