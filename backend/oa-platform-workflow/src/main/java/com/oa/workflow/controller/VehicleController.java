package com.oa.workflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.common.core.result.R;
import com.oa.workflow.entity.SysVehicle;
import com.oa.workflow.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用车申请控制器
 */
@Tag(name = "用车申请管理")
@RestController
@RequestMapping("/api/workflow/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "用车申请分页列表")
    @GetMapping("/list")
    public R<?> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {
        return R.ok(vehicleService.getVehicleList(pageNum, pageSize, status, userId));
    }

    @Operation(summary = "用车申请详情（含审批历史）")
    @GetMapping("/{id}")
    public R<?> getDetail(@PathVariable Long id) {
        return R.ok(vehicleService.getVehicleDetail(id));
    }

    @Operation(summary = "创建用车申请")
    @PostMapping
    public R<?> create(@RequestBody VehicleRequest request) {
        Long id = vehicleService.createVehicle(request.getData(), request.getUserId(), request.getUserName());
        return R.ok(id);
    }

    @Operation(summary = "更新用车申请")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody SysVehicle data) {
        vehicleService.updateVehicle(id, data);
        return R.ok();
    }

    @Operation(summary = "删除用车申请")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return R.ok();
    }

    @Operation(summary = "提交送审")
    @PostMapping("/{id}/submit")
    public R<?> submit(@PathVariable Long id, @RequestBody SubmitRequest request) {
        vehicleService.submitVehicle(id, request.getUserId(), request.getUserName());
        return R.ok("提交成功");
    }

    @Operation(summary = "审批通过")
    @PutMapping("/{id}/approve")
    public R<?> approve(@PathVariable Long id, @RequestBody ApproveRequest request) {
        vehicleService.approveVehicle(id, request.getUserId(), request.getUserName(), request.getComment());
        return R.ok("审批通过");
    }

    @Operation(summary = "驳回申请")
    @PutMapping("/{id}/reject")
    public R<?> reject(@PathVariable Long id, @RequestBody RejectRequest request) {
        vehicleService.rejectVehicle(id, request.getUserId(), request.getUserName(), request.getReason());
        return R.ok("已驳回");
    }

    @Operation(summary = "我的待办任务")
    @GetMapping("/my-tasks")
    public R<?> getMyTasks(@RequestParam Long userId) {
        return R.ok(vehicleService.getMyTasks(userId));
    }

    // ========== Request DTOs ==========

    @Data
    public static class VehicleRequest {
        private SysVehicle data;
        private Long userId;
        private String userName;
    }

    @Data
    public static class SubmitRequest {
        private Long userId;
        private String userName;
    }

    @Data
    public static class ApproveRequest {
        private Long userId;
        private String userName;
        private String comment;
    }

    @Data
    public static class RejectRequest {
        private Long userId;
        private String userName;
        private String reason;
    }
}
