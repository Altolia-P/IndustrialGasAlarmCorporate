package com.niit.industrialgasalarmcorporate.application.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateContentDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不超过200字符")
    private String title;

    @Size(max = 500, message = "摘要不超过500字符")
    private String summary;

    @Size(max = 10000, message = "正文不超过10000字符")
    private String body;

    @Size(max = 500, message = "封面图URL不超过500字符")
    private String coverImage;

    @NotBlank(message = "内容类型不能为空")
    @Size(max = 50, message = "内容类型不超过50字符")
    private String type;

    @NotBlank(message = "所属分类不能为空")
    @Size(max = 36, message = "分类UUID不超过36字符")
    private String categoryUuid;

    @Size(max = 20, message = "状态不超过20字符")
    private String status;
}
