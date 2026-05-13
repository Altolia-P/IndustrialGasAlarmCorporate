package com.niit.industrialgasalarmcorporate.application.content.vo;

import lombok.Data;

@Data
public class ContentDetailVO {

    private String contentUuid;
    private String title;
    private String body;
    private String coverImage;
    private String type;
    private String categoryUuid;
    private String categoryName;
    private String status;
    private String createdAt;
    private String updatedAt;
}
