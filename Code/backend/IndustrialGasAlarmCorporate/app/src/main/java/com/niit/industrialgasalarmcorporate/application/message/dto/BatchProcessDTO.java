package com.niit.industrialgasalarmcorporate.application.message.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchProcessDTO {

    @NotEmpty(message = "留言ID列表不能为空")
    private List<String> uuids;

    private String remark;
}
