package com.niit.industrialgasalarmcorporate.application.message.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BatchProcessDTO {

    @NotEmpty(message = "留言ID列表不能为空")
    @Size(max = 100, message = "单次批量处理不超过100条")
    private List<String> uuids;

    private String remark;
}
