package com.oa.workflow.controller;

import com.oa.common.core.result.R;
import com.oa.workflow.service.IncomingDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收文审批控制器
 */
@Tag(name = "收文审批")
@RestController
@RequestMapping("/api/workflow/incoming")
@RequiredArgsConstructor
public class IncomingDocumentController {

    private final IncomingDocumentService incomingDocumentService;

    @Operation(summary = "收文提交送审")
    @PostMapping("/{id}/submit")
    public R<?> submit(@PathVariable Long id, @RequestBody SubmitRequest request) {
        incomingDocumentService.submit(id, request.getUserId());
        return R.ok("提交成功");
    }

    @Operation(summary = "收文审批通过")
    @PutMapping("/{id}/approve")
    public R<?> approve(@PathVariable Long id, @RequestBody ApproveRequest request) {
        incomingDocumentService.approve(id, request.getUserId());
        return R.ok("审批通过");
    }

    @Operation(summary = "收文驳回")
    @PutMapping("/{id}/reject")
    public R<?> reject(@PathVariable Long id, @RequestBody RejectRequest request) {
        incomingDocumentService.reject(id, request.getUserId(), request.getReason());
        return R.ok("已驳回");
    }

    @Operation(summary = "收文加签：为当前节点追加审批人（会签）")
    @PostMapping("/{id}/add-sign")
    public R<?> addSign(@PathVariable Long id, @RequestBody AddSignRequest request) {
        incomingDocumentService.addSign(id, request.getUserId(), request.getUserName());
        return R.ok("加签成功");
    }

    @Operation(summary = "收文减签：移除指定审批人")
    @DeleteMapping("/{id}/remove-sign/{taskId}/{userId}")
    public R<?> removeSign(@PathVariable Long id,
                           @PathVariable String taskId,
                           @PathVariable Long userId) {
        incomingDocumentService.removeSign(id, taskId, userId);
        return R.ok("减签成功");
    }

    @Operation(summary = "收文退回：退回上一审批节点")
    @PutMapping("/{id}/return")
    public R<?> returnStep(@PathVariable Long id, @RequestBody ReturnRequest request) {
        incomingDocumentService.returnStep(id, request.getUserId(), request.getUserName(),
                                            request.getTargetTaskId(), request.getReason());
        return R.ok("退回成功");
    }

    @Operation(summary = "收文待办任务")
    @GetMapping("/my-tasks")
    public R<?> getMyTasks(@RequestParam Long userId) {
        return R.ok(incomingDocumentService.getMyTasks(userId));
    }

    @Operation(summary = "收文审批历史")
    @GetMapping("/{id}/history")
    public R<?> getHistory(@PathVariable Long id) {
        return R.ok(incomingDocumentService.getApprovalHistory(id));
    }

    // ========== Request DTOs ==========

    @Data
    public static class SubmitRequest {
        private Long userId;
    }

    @Data
    public static class ApproveRequest {
        private Long userId;
    }

    @Data
    public static class RejectRequest {
        private Long userId;
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
