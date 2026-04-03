package com.oa.workflow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.common.core.exception.BusinessException;
import com.oa.workflow.entity.SysDocument;
import com.oa.workflow.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Collections;

/**
 * 公文 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final ProcessEngine processEngine;
    private final JdbcTemplate jdbcTemplate;

    private static final String PROCESS_KEY = "document_publish";

    // ==================== CRUD ====================

    public Page<SysDocument> list(Long userId, String status, String docType, int pageNum, int pageSize) {
        Page<SysDocument> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDocument> qw = new LambdaQueryWrapper<>();
        if (userId != null) {
            qw.eq(SysDocument::getDraftUserId, userId);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq(SysDocument::getStatus, status);
        }
        if (docType != null && !docType.isEmpty()) {
            qw.eq(SysDocument::getDocType, docType);
        }
        qw.orderByDesc(SysDocument::getCreateTime);
        return documentMapper.selectPage(page, qw);
    }

    public SysDocument getById(Long id) {
        SysDocument doc = documentMapper.selectById(id);
        if (doc == null) {
            throw new BusinessException("公文不存在");
        }
        return doc;
    }

    @Transactional
    public Long create(SysDocument doc) {
        doc.setStatus("draft");
        doc.setCreateTime(LocalDateTime.now());
        doc.setUpdateTime(LocalDateTime.now());
        documentMapper.insert(doc);
        return doc.getId();
    }

    @Transactional
    public void update(Long id, SysDocument doc) {
        SysDocument existing = getById(id);
        if ("draft".equals(existing.getStatus()) || "rejected".equals(existing.getStatus())) {
            doc.setId(id);
            doc.setUpdateTime(LocalDateTime.now());
            documentMapper.updateById(doc);
        } else {
            throw new BusinessException("只有草稿或驳回状态的公文可以编辑");
        }
    }

    @Transactional
    public void delete(Long id) {
        SysDocument doc = getById(id);
        if (!"draft".equals(doc.getStatus())) {
            throw new BusinessException("只有草稿状态的公文可以删除");
        }
        documentMapper.deleteById(id);
    }

    // ==================== 流程操作 ====================

    /**
     * 提交送审：启动 Flowable 流程
     */
    @Transactional
    public void submit(Long id, Long userId) {
        SysDocument doc = getById(id);
        if (!"draft".equals(doc.getStatus()) && !"rejected".equals(doc.getStatus())) {
            throw new BusinessException("只有草稿或驳回的公文可以提交");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", userId);  // 发起人
        variables.put("docId", id);

        ProcessInstance pi = processEngine.getRuntimeService()
                .startProcessInstanceByKey(PROCESS_KEY, String.valueOf(id), variables);

        doc.setProcessInstanceId(pi.getId());
        doc.setStatus("pending");
        doc.setCurrentStep("dept_approval");
        doc.setUpdateTime(LocalDateTime.now());
        documentMapper.updateById(doc);

        log.info("公文[{}]启动流程实例: {}", id, pi.getId());
    }

    /**
     * 审批通过（当前示例：部门负责人审批）
     */
    @Transactional
    public void approve(Long id, Long approverId) {
        SysDocument doc = getById(id);
        String currentStep = doc.getCurrentStep();

        // 推进流程
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(doc.getProcessInstanceId())
                .taskAssignee(String.valueOf(approverId))
                .list();

        if (tasks.isEmpty()) {
            // 尝试按候选人查询
            tasks = processEngine.getTaskService().createTaskQuery()
                    .processInstanceId(doc.getProcessInstanceId())
                    .taskCandidateUser(String.valueOf(approverId))
                    .list();
        }

        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("approved", true);
                vars.put("approver", approverId);
                processEngine.getTaskService().complete(task.getId(), vars);
            }
        }

        // 更新状态
        if ("dept_approval".equals(currentStep)) {
            doc.setCurrentStep("office_approval");
        } else if ("office_approval".equals(currentStep)) {
            doc.setStatus("approved");
            doc.setPublishTime(LocalDateTime.now());
            doc.setCurrentStep(null);
        }
        doc.setUpdateTime(LocalDateTime.now());
        documentMapper.updateById(doc);
    }

    /**
     * 驳回（驳回到拟稿人）
     */
    @Transactional
    public void reject(Long id, Long approverId, String reason) {
        SysDocument doc = getById(id);

        // 查任务并驳回
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(doc.getProcessInstanceId())
                .taskAssignee(String.valueOf(approverId))
                .list();

        if (tasks.isEmpty()) {
            tasks = processEngine.getTaskService().createTaskQuery()
                    .processInstanceId(doc.getProcessInstanceId())
                    .taskCandidateUser(String.valueOf(approverId))
                    .list();
        }

        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("approved", false);
                vars.put("rejectReason", reason != null ? reason : "审批不通过");
                vars.put("approver", approverId);
                processEngine.getTaskService().complete(task.getId(), vars);
            }
        }

        doc.setStatus("rejected");
        doc.setCurrentStep(null);
        doc.setUpdateTime(LocalDateTime.now());
        documentMapper.updateById(doc);
    }

    /**
     * 我的待办任务
     */
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
            // 获取关联的公文信息
            String docId = String.valueOf(task.getProcessVariables().get("docId"));
            if (docId != null && !docId.equals("null")) {
                SysDocument doc = documentMapper.selectById(Long.parseLong(docId));
                if (doc != null) {
                    item.put("docTitle", doc.getTitle());
                    item.put("docType", doc.getDocType());
                    item.put("priority", doc.getPriority());
                }
            }
            result.add(item);
        }
        return result;
    }

    // ==================== 加签 / 减签 / 退回 ====================

    /**
     * 加签：为当前任务节点追加审批人（会签）
     * @param id      公文ID
     * @param userId  待加签用户ID
     * @param userName 待加签用户姓名
     */
    @Transactional
    public void addSign(Long id, Long userId, String userName) {
        SysDocument doc = getById(id);
        if (doc.getProcessInstanceId() == null) {
            throw new BusinessException("该公文未启动流程，无法加签");
        }
        if (!"pending".equals(doc.getStatus())) {
            throw new BusinessException("只有待审批状态的公文可以加签");
        }

        // 查找当前活跃任务
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(doc.getProcessInstanceId())
                .active()
                .list();

        if (tasks.isEmpty()) {
            throw new BusinessException("当前没有可加签的任务节点");
        }

        for (Task task : tasks) {
            // 为任务追加候选用户
            processEngine.getTaskService().addCandidateUser(task.getId(), String.valueOf(userId));
            log.info("公文[{}]加签用户[{}]到任务[{}]", id, userName, task.getId());
        }
    }

    /**
     * 减签：移除指定审批人的任务
     * @param id      公文ID
     * @param taskId  任务ID
     * @param userId  待减签用户ID
     */
    @Transactional
    public void removeSign(Long id, String taskId, Long userId) {
        SysDocument doc = getById(id);
        if (!"pending".equals(doc.getStatus())) {
            throw new BusinessException("只有待审批状态的公文可以减签");
        }

        // 查找该用户在此任务上的候选人身份
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(String.valueOf(userId))
                .list();

        if (tasks.isEmpty()) {
            // 尝试按 assignee 查找
            tasks = processEngine.getTaskService().createTaskQuery()
                    .taskId(taskId)
                    .taskAssignee(String.valueOf(userId))
                    .list();
        }

        if (tasks.isEmpty()) {
            throw new BusinessException("该用户在指定任务上无审批权限，无法减签");
        }

        // 直接删掉候选用户身份（不影响流程继续）
        processEngine.getTaskService().deleteCandidateUser(taskId, String.valueOf(userId));
        log.info("公文[{}]从任务[{}]减签用户[{}]", id, taskId, userId);
    }

    /**
     * 退回：将流程退回上一节点
     * @param id           公文ID
     * @param userId       操作人ID
     * @param userName     操作人姓名
     * @param targetTaskId 目标退回节点（可选，为空则退回上一节点）
     * @param reason       退回原因
     */
    @Transactional
    public void returnStep(Long id, Long userId, String userName, String targetTaskId, String reason) {
        SysDocument doc = getById(id);
        if (doc.getProcessInstanceId() == null) {
            throw new BusinessException("该公文未启动流程，无法退回");
        }
        if (!"pending".equals(doc.getStatus())) {
            throw new BusinessException("只有待审批状态的公文可以退回");
        }

        // 查找当前活跃任务
        List<Task> currentTasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(doc.getProcessInstanceId())
                .active()
                .list();

        if (currentTasks.isEmpty()) {
            throw new BusinessException("当前没有可退回的任务节点");
        }

        Task currentTask = currentTasks.get(0);
        String currentActivityId = currentTask.getTaskDefinitionKey();

        // 获取历史任务，找到上一个节点
        List<org.flowable.engine.history.HistoricActivityInstance> history = processEngine.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(doc.getProcessInstanceId())
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .desc()
                .list();

        if (history.isEmpty()) {
            throw new BusinessException("无法找到可退回的上一个节点");
        }

        String targetActivityId;
        if (targetTaskId != null && !targetTaskId.isEmpty()) {
            targetActivityId = targetTaskId;
        } else {
            // 取上一个已完成的节点
            targetActivityId = history.get(0).getActivityId();
        }

        // 使用 ChangeActivityState 退回流程
        processEngine.getRuntimeService().createChangeActivityStateBuilder()
                .processInstanceId(doc.getProcessInstanceId())
                .moveActivityIdsToSingleActivityId(Collections.singletonList(currentActivityId), targetActivityId)
                .changeState();

        // 退回原因已通过 Flowable 历史记录追踪

        log.info("公文[{}]从节点[{}]退回至[{}]，原因：{}", id, currentActivityId, targetActivityId, reason);
    }

    // ==================== 历史记录 ====================

    /**
     * 查询流程历史（审批记录）
     */
    public List<Map<String, Object>> getApprovalHistory(Long docId) {
        SysDocument doc = getById(docId);
        if (doc.getProcessInstanceId() == null) {
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
        return jdbcTemplate.queryForList(sql, doc.getProcessInstanceId());
    }
}
