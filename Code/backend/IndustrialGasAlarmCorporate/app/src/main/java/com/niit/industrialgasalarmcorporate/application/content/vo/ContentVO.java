package com.niit.industrialgasalarmcorporate.application.content.vo;

import lombok.Data;

@Data
public class ContentVO {

    private String contentUuid;
    private String title;
    private String summary;
    private String coverImage;
    private String type;
    private String categoryUuid;
    private String categoryName;
    private String status;
    private String createdAt;
}
