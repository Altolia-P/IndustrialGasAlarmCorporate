package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_download_file")
public class DownloadFilePO {

    @TableId
    private String downloadUuid;

    private String displayName;

    private String originalName;

    private Long fileSize;

    private String contentType;

    private String storedPath;

    private LocalDateTime createdAt;
}
