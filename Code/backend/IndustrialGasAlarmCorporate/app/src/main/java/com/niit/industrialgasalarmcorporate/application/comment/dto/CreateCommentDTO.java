package com.niit.industrialgasalarmcorporate.application.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentDTO {

    @NotBlank(message = "请填写评论内容")
    @Size(min = 1, max = 500, message = "评论内容1-500字")
    private String content;
}
