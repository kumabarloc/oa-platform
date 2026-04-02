package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.system.service.LoginLogService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/system/login-log")
public class LoginLogController {
    private final LoginLogService service;
    public LoginLogController(LoginLogService service) { this.service = service; }
    @GetMapping("/list")
    public R<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {
        return R.ok(service.getList(pageNum, pageSize, username, status));
    }
}
