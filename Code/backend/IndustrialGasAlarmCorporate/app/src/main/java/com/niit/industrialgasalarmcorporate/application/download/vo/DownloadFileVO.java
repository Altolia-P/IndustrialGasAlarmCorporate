package com.niit.industrialgasalarmcorporate.application.download.vo;

import lombok.Data;

@Data
public class DownloadFileVO {
    private String downloadUuid;
    private String displayName;
    private String originalName;
    private long fileSize;
    private String contentType;
    private String storedPath;
    private String createdAt;
}
