package com.oa.workflow.service.impl;

import com.oa.workflow.service.IncomingDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 收文审批 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IncomingDocumentServiceImpl implements IncomingDocumentService {

    private final ProcessEngine processEngine;
    private final JdbcTemplate jdbcTemplate;

    private static final String PROCESS_KEY = "incoming_document";

    // ==================== 辅助方法 ====================

    private Map<String, Object> getIncomingDoc(Long id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT * FROM sys_incoming_document WHERE id = ?", id);
        if (rows.isEmpty()) {
            throw new RuntimeException("收文不存在: id=" + id);
        }
        return rows.get(0);
    }

    private void updateField(Long id, String field, Object value) {
        jdbcTemplate.update(
            "UPDATE sys_incoming_document SET " + field + " = ?, update_time = ? WHERE id = ?",
            value, LocalDateTime.now(), id);
    }

    private String getProcessInstanceId(Long id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT process_instance_id FROM sys_incoming_document WHERE id = ?", id);
        if (rows.isEmpty() || rows.get(0).get("process_instance_id") == null) {
            return null;
        }
        return rows.get(0).get("process_instance_id").toString();
    }

    private List<Task> findTasks(Long docId, Long userId) {
        String piId = getProcessInstanceId(docId);
        if (piId == null) return Collections.emptyList();

        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(piId)
                .taskAssignee(String.valueOf(userId))
                .active()
                .list();

        if (tasks.isEmpty()) {
            tasks = processEngine.getTaskService().createTaskQuery()
                    .processInstanceId(piId)
                    .taskCandidateUser(String.valueOf(userId))
                    .active()
                    .list();
        }
        return tasks;
    }

    // ==================== 流程操作 ====================

    /**
     * 提交送审：启动 Flowable 流程
     */
    @Override
    @Transactional
    public void submit(Long id, Long userId) {
        Map<String, Object> doc = getIncomingDoc(id);
        String status = (String) doc.get("status");

        if (!"received".equals(status) && !"draft".equals(status)) {
            throw new RuntimeException("只有收到或草稿状态的收文可以提交");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", userId);
        variables.put("docId", id);

        ProcessInstance pi = processEngine.getRuntimeService()
                .startProcessInstanceByKey(PROCESS_KEY, String.valueOf(id), variables);

        updateField(id, "process_instance_id", pi.getId());
        updateField(id, "status", "pending");
        updateField(id, "current_step", "leader_approval");

        log.info("收文[{}]启动流程实例: {}", id, pi.getId());
    }

    /**
     * 审批通过
     */
    @Override
    @Transactional
    public void approve(Long id, Long approverId) {
        Map<String, Object> doc = getIncomingDoc(id);
        String currentStep = (String) doc.get("current_step");

        List<Task> tasks = findTasks(id, approverId);
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("approved", true);
                vars.put("approver", approverId);
                processEngine.getTaskService().complete(task.getId(), vars);
            }
        }

        // 更新步骤状态
        if ("leader_approval".equals(currentStep)) {
            updateField(id, "current_step", "office_approval");
        } else if ("office_approval".equals(currentStep)) {
            updateField(id, "status", "approved");
            updateField(id, "current_step", null);
        }
    }

    /**
     * 驳回：直接拒绝，退回拟稿人
     */
    @Override
    @Transactional
    public void reject(Long id, Long userId, String reason) {
        List<Task> tasks = findTasks(id, userId);
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("approved", false);
                vars.put("rejectReason", reason != null ? reason : "审批不通过");
                vars.put("approver", userId);
                processEngine.getTaskService().complete(task.getId(), vars);
            }
        }

        updateField(id, "status", "rejected");
        updateField(id, "current_step", null);

        log.info("收文[{}]被用户[{}]驳回，原因：{}", id, userId, reason);
    }

    // ==================== 加签 / 减签 / 退回 ====================

    /**
     * 加签：为当前任务节点追加审批人（会签）
     */
    @Override
    @Transactional
    public void addSign(Long id, Long userId, String userName) {
        String piId = getProcessInstanceId(id);
        if (piId == null) {
            throw new RuntimeException("该收文未启动流程，无法加签");
        }

        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(piId)
                .active()
                .list();

        if (tasks.isEmpty()) {
            throw new RuntimeException("当前没有可加签的任务节点");
        }

        for (Task task : tasks) {
            processEngine.getTaskService().addCandidateUser(task.getId(), String.valueOf(userId));
            log.info("收文[{}]加签用户[{}]到任务[{}]", id, userName, task.getId());
        }
    }

    /**
     * 减签：移除指定审批人
     */
    @Override
    @Transactional
    public void removeSign(Long id, String taskId, Long userId) {
        processEngine.getTaskService().deleteCandidateUser(taskId, String.valueOf(userId));
        log.info("收文[{}]从任务[{}]减签用户[{}]", id, taskId, userId);
    }

    /**
     * 退回：退回上一节点
     */
    @Override
    @Transactional
    public void returnStep(Long id, Long userId, String userName, String targetTaskId, String reason) {
        String piId = getProcessInstanceId(id);
        if (piId == null) {
            throw new RuntimeException("该收文未启动流程，无法退回");
        }

        List<Task> currentTasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(piId)
                .active()
                .list();

        if (currentTasks.isEmpty()) {
            throw new RuntimeException("当前没有可退回的任务节点");
        }

        Task currentTask = currentTasks.get(0);
        String currentActivityId = currentTask.getTaskDefinitionKey();

        List<org.flowable.engine.history.HistoricActivityInstance> history =
            processEngine.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(piId)
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .desc()
                .list();

        if (history.isEmpty()) {
            throw new RuntimeException("无法找到可退回的上一个节点");
        }

        String targetActivityId = (targetTaskId != null && !targetTaskId.isEmpty())
                ? targetTaskId
                : history.get(0).getActivityId();

        processEngine.getRuntimeService().createChangeActivityStateBuilder()
                .processInstanceId(piId)
                .moveActivityIdsToSingleActivityId(java.util.Collections.singletonList(currentActivityId), targetActivityId)
                .changeState();

                // 退回原因已通过 Flowable 历史记录追踪

        log.info("收文[{}]从节点[{}]退回至[{}]，原因：{}", id, currentActivityId, targetActivityId, reason);
    }

    // ==================== 查询 ====================

    /**
     * 获取当前用户待办任务
     */
    @Override
    public List<Map<String, Object>> getMyTasks(Long userId) {
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .taskAssignee(String.valueOf(userId))
                .active()
                .list();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("taskId", task.getId());
            item.put("taskName", task.getName());
            item.put("processInstanceId", task.getProcessInstanceId());
            item.put("docId", task.getProcessVariables().get("docId"));
            item.put("createTime", task.getCreateTime());

            Object docId = task.getProcessVariables().get("docId");
            if (docId != null && !"null".equals(docId.toString())) {
                try {
                    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                        "SELECT title, doc_number FROM sys_incoming_document WHERE id = ?",
                        Long.parseLong(docId.toString()));
                    if (!rows.isEmpty()) {
                        item.put("docTitle", rows.get(0).get("title"));
                        item.put("docNumber", rows.get(0).get("doc_number"));
                    }
                } catch (Exception ignored) {}
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 查询审批历史
     */
    @Override
    public List<Map<String, Object>> getApprovalHistory(Long docId) {
        String piId = getProcessInstanceId(docId);
        if (piId == null) {
            return Collections.emptyList();
        }

        String sql = """
            SELECT TASK_NAME_ as taskName, ASSIGNEE_ as assignee,
                   START_TIME_ as startTime, END_TIME_ as endTime,
                   DELETE_REASON_ as deleteReason
            FROM ACT_HI_TASKINST
            WHERE PROC_INST_ID_ = ?
            ORDER BY START_TIME_ ASC
            """;
        return jdbcTemplate.queryForList(sql, piId);
    }
}
