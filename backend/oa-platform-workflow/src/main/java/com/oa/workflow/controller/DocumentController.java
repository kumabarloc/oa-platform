package com.oa.workflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.common.core.result.R;
import com.oa.workflow.entity.SysDocument;
import com.oa.workflow.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公文管理控制器
 */
@Tag(name = "公文管理")
@RestController
@RequestMapping("/api/workflow/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "获取公文列表")
    @GetMapping("/list")
    public R<?> getList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String docType,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(documentService.list(userId, status, docType, pageNum, pageSize));
    }

    @Operation(summary = "获取公文详情")
    @GetMapping("/{id}")
    public R<?> getDetail(@PathVariable Long id) {
        return R.ok(documentService.getById(id));
    }

    @Operation(summary = "创建公文")
    @PostMapping
    public R<?> create(@RequestBody SysDocument doc) {
        Long id = documentService.create(doc);
        return R.ok(id);
    }

    @Operation(summary = "更新公文")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody SysDocument doc) {
        documentService.update(id, doc);
        return R.ok();
    }

    @Operation(summary = "删除公文")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        documentService.delete(id);
        return R.ok();
    }

    @Operation(summary = "提交送审")
    @PostMapping("/{id}/submit")
    public R<?> submit(@PathVariable Long id, @RequestBody SubmitRequest request) {
        documentService.submit(id, request.getUserId());
        return R.ok("提交成功");
    }

    @Operation(summary = "审批通过")
    @PutMapping("/{id}/approve")
    public R<?> approve(@PathVariable Long id, @RequestBody ApproveRequest request) {
        documentService.approve(id, request.getApproverId());
        return R.ok("审批通过");
    }

    @Operation(summary = "驳回")
    @PutMapping("/{id}/reject")
    public R<?> reject(@PathVariable Long id, @RequestBody RejectRequest request) {
        documentService.reject(id, request.getApproverId(), request.getReason());
        return R.ok("已驳回");
    }

    @Operation(summary = "加签：为当前节点追加审批人（会签）")
    @PostMapping("/{id}/add-sign")
    public R<?> addSign(@PathVariable Long id, @RequestBody AddSignRequest request) {
        documentService.addSign(id, request.getUserId(), request.getUserName());
        return R.ok("加签成功");
    }

    @Operation(summary = "减签：移除指定审批人")
    @DeleteMapping("/{id}/remove-sign/{taskId}/{userId}")
    public R<?> removeSign(@PathVariable Long id,
                           @PathVariable String taskId,
                           @PathVariable Long userId) {
        documentService.removeSign(id, taskId, userId);
        return R.ok("减签成功");
    }

    @Operation(summary = "退回：退回上一审批节点")
    @PutMapping("/{id}/return")
    public R<?> returnStep(@PathVariable Long id, @RequestBody ReturnRequest request) {
        documentService.returnStep(id, request.getUserId(), request.getUserName(),
                                   request.getTargetTaskId(), request.getReason());
        return R.ok("退回成功");
    }

    @Operation(summary = "我的待办任务")
    @GetMapping("/my-tasks")
    public R<?> getMyTasks(@RequestParam Long userId) {
        return R.ok(documentService.getMyTasks(userId));
    }

    @Operation(summary = "审批历史")
    @GetMapping("/{id}/history")
    public R<?> getHistory(@PathVariable Long id) {
        return R.ok(documentService.getApprovalHistory(id));
    }

    // ========== Request DTOs ==========

    @Data
    public static class SubmitRequest {
        private Long userId;
    }

    @Data
    public static class ApproveRequest {
        private Long approverId;
    }

    @Data
    public static class RejectRequest {
        private Long approverId;
        private String reason;
    }

    @Data
    public static class AddSignRequest {
        private Long userId;
        private String userName;
    }

    @Data
    public static class ReturnRequest {
        private Long userId;
        private String userName;
        private String targetTaskId;
        private String reason;
    }
}
