package com.niit.industrialgasalarmcorporate.application.comment.vo;

import lombok.Data;

@Data
public class CommentVO {

    private String commentUuid;
    private String targetType;
    private String targetUuid;
    private String authorType;
    private String authorUuid;
    private String authorName;
    private String content;
    private String createdAt;
}
