package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.system.service.IncomingDocumentService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow/incoming")
public class IncomingDocumentController {

    private final IncomingDocumentService service;

    public IncomingDocumentController(IncomingDocumentService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public R<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok(service.getList(pageNum, pageSize, status, startDate, endDate));
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(service.getDetail(id));
    }

    @PostMapping
    public R<?> create(@RequestBody Map<String, Object> params) {
        return R.ok(service.create(params));
    }

    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        service.update(id, params);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }

    @PutMapping("/{id}/distribute")
    public R<?> distribute(@PathVariable Long id) {
        service.distribute(id);
        return R.ok();
    }

    @PutMapping("/{id}/archive")
    public R<?> archive(@PathVariable Long id) {
        service.archive(id);
        return R.ok();
    }
}
