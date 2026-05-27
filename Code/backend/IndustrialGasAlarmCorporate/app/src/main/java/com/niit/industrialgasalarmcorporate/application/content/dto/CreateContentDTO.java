package com.niit.industrialgasalarmcorporate.application.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateContentDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不超过200字符")
    private String title;

    private String summary;

    private String body;

    private String coverImage;

    @NotBlank(message = "内容类型不能为空")
    private String type;

    @NotBlank(message = "所属分类不能为空")
    private String categoryUuid;

    private String status;
}
