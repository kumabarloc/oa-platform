package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.system.service.OperationLogService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/system/operation-log")
public class OperationLogController {
    private final OperationLogService service;
    public OperationLogController(OperationLogService service) { this.service = service; }
    @GetMapping("/list")
    public R<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operName) {
        return R.ok(service.getList(pageNum, pageSize, title, operName));
    }
}
