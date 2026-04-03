package com.oa.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 收文审批 Service 接口
 */
public interface IncomingDocumentService {

    // ==================== 收文 CRUD（由 com.oa.system.IncomingDocumentService 提供） ====================

    // 以下为工作流专用操作

    /**
     * 收文提交送审（启动 Flowable 流程）
     */
    void submit(Long id, Long userId);

    /**
     * 收文审批通过
     */
    void approve(Long id, Long approverId);

    /**
     * 驳回：直接拒绝，退回拟稿人
     */
    void reject(Long id, Long userId, String reason);

    /**
     * 加签：为当前节点追加审批人（会签）
     */
    void addSign(Long id, Long userId, String userName);

    /**
     * 减签：移除指定审批人
     */
    void removeSign(Long id, String taskId, Long userId);

    /**
     * 退回：退回上一节点
     */
    void returnStep(Long id, Long userId, String userName, String targetTaskId, String reason);

    /**
     * 获取当前用户待办任务
     */
    List<Map<String, Object>> getMyTasks(Long userId);

    /**
     * 查询审批历史
     */
    List<Map<String, Object>> getApprovalHistory(Long docId);
}
