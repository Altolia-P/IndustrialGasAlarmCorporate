package com.niit.industrialgasalarmcorporate.application.content.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateContentDTO {

    @Size(max = 200, message = "标题不超过200字符")
    private String title;

    private String summary;

    private String body;

    private String coverImage;

    private String categoryUuid;

    private String status;
}
