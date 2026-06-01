package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.operationlog.service.OperationLogService;
import com.niit.industrialgasalarmcorporate.application.operationlog.vo.OperationLogVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminOperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping("/operation-logs")
    public Result<Page<OperationLogVO>> list(
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String targetType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(operationLogService.listWithFilter(operatorName, operation, targetType, page, size));
    }

    @GetMapping("/operation-logs/{logId}")
    public Result<OperationLogVO> getById(@PathVariable String logId) {
        OperationLogVO vo = operationLogService.getById(logId);
        return vo != null ? Result.ok(vo) : Result.ok(null);
    }
}
