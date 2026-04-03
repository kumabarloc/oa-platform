package com.oa.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.common.core.exception.BusinessException;
import com.oa.workflow.entity.SysVehicle;
import com.oa.workflow.entity.SysVehicleHistory;
import com.oa.workflow.mapper.VehicleHistoryMapper;
import com.oa.workflow.mapper.VehicleMapper;
import com.oa.workflow.service.VehicleService;
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
 * 用车申请 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleHistoryMapper vehicleHistoryMapper;
    private final ProcessEngine processEngine;
    private final JdbcTemplate jdbcTemplate;

    private static final String PROCESS_KEY = "vehicle_apply";

    // ==================== CRUD ====================

    @Override
    public Page<SysVehicle> getVehicleList(int pageNum, int pageSize, String status, Long userId) {
        Page<SysVehicle> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysVehicle> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            qw.eq(SysVehicle::getStatus, status);
        }
        if (userId != null) {
            qw.eq(SysVehicle::getDraftUserId, userId);
        }
        qw.orderByDesc(SysVehicle::getCreateTime);
        return vehicleMapper.selectPage(page, qw);
    }

    @Override
    public Map<String, Object> getVehicleDetail(Long id) {
        SysVehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException("用车申请不存在");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("vehicle", vehicle);

        // 查询审批历史
        LambdaQueryWrapper<SysVehicleHistory> qw = new LambdaQueryWrapper<>();
        qw.eq(SysVehicleHistory::getVehicleId, id);
        qw.orderByAsc(SysVehicleHistory::getStartTime);
        result.put("history", vehicleHistoryMapper.selectList(qw));

        return result;
    }

    @Override
    @Transactional
    public Long createVehicle(SysVehicle data, Long userId, String userName) {
        data.setDraftUserId(userId);
        data.setDraftUserName(userName);
        data.setStatus("draft");
        data.setCreateTime(LocalDateTime.now());
        data.setUpdateTime(LocalDateTime.now());
        if (data.getPassengerCount() == null) {
            data.setPassengerCount(1);
        }
        if (data.getPriority() == null) {
            data.setPriority("normal");
        }
        vehicleMapper.insert(data);
        return data.getId();
    }

    @Override
    @Transactional
    public void updateVehicle(Long id, SysVehicle data) {
        SysVehicle existing = vehicleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("用车申请不存在");
        }
        if (!"draft".equals(existing.getStatus()) && !"rejected".equals(existing.getStatus())) {
            throw new BusinessException("只有草稿或驳回状态的申请可以编辑");
        }
        data.setId(id);
        data.setUpdateTime(LocalDateTime.now());
        data.setStatus(null); // 不允许改状态
        data.setProcessInstanceId(null);
        data.setCurrentStep(null);
        vehicleMapper.updateById(data);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        SysVehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException("用车申请不存在");
        }
        if (!"draft".equals(vehicle.getStatus())) {
            throw new BusinessException("只有草稿状态的申请可以删除");
        }
        vehicle.setDelFlag("1");
        vehicleMapper.updateById(vehicle);
    }

    // ==================== 流程操作 ====================

    @Override
    @Transactional
    public void submitVehicle(Long id, Long userId, String userName) {
        SysVehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException("用车申请不存在");
        }
        if (!"draft".equals(vehicle.getStatus()) && !"rejected".equals(vehicle.getStatus())) {
            throw new BusinessException("只有草稿或驳回的申请可以提交");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", userId);
        variables.put("vehicleId", id);

        ProcessInstance pi = processEngine.getRuntimeService()
                .startProcessInstanceByKey(PROCESS_KEY, String.valueOf(id), variables);

        vehicle.setProcessInstanceId(pi.getId());
        vehicle.setStatus("pending");
        vehicle.setCurrentStep("dept_approval");
        vehicle.setUpdateTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);

        // 记录历史
        saveHistory(id, null, "提交申请", userId, userName, "submit", "提交送审", LocalDateTime.now());

        log.info("用车申请[{}]启动流程实例: {}", id, pi.getId());
    }

    @Override
    @Transactional
    public void approveVehicle(Long id, Long userId, String userName, String comment) {
        SysVehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException("用车申请不存在");
        }
        String currentStep = vehicle.getCurrentStep();

        // 查找当前用户可办理的任务
        List<Task> tasks = findTasks(vehicle.getProcessInstanceId(), userId);

        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("approved", true);
                vars.put("approver", userId);
                processEngine.getTaskService().complete(task.getId(), vars);

                // 记录历史
                saveHistory(id, task.getId(), task.getName(), userId, userName, "approve",
                        comment != null ? comment : "审批通过", LocalDateTime.now());
            }
        }

        // 更新申请状态
        if ("dept_approval".equals(currentStep)) {
            vehicle.setCurrentStep("driver_dispatch");
        } else if ("driver_dispatch".equals(currentStep)) {
            vehicle.setStatus("approved");
            vehicle.setCurrentStep(null);
        }
        vehicle.setUpdateTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);
    }

    @Override
    @Transactional
    public void rejectVehicle(Long id, Long userId, String userName, String reason) {
        SysVehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException("用车申请不存在");
        }

        // 查找当前用户可办理的任务
        List<Task> tasks = findTasks(vehicle.getProcessInstanceId(), userId);

        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("approved", false);
                vars.put("rejectReason", reason != null ? reason : "审批不通过");
                vars.put("approver", userId);
                processEngine.getTaskService().complete(task.getId(), vars);

                // 记录历史
                saveHistory(id, task.getId(), task.getName(), userId, userName, "reject",
                        reason != null ? reason : "审批驳回", LocalDateTime.now());
            }
        }

        vehicle.setStatus("rejected");
        vehicle.setCurrentStep(null);
        vehicle.setUpdateTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);
    }

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
            item.put("createTime", task.getCreateTime());

            // 获取关联的用车申请信息
            Object vehicleIdObj = task.getProcessVariables().get("vehicleId");
            if (vehicleIdObj != null) {
                String vehicleIdStr = String.valueOf(vehicleIdObj);
                if (!"null".equals(vehicleIdStr)) {
                    SysVehicle v = vehicleMapper.selectById(Long.parseLong(vehicleIdStr));
                    if (v != null) {
                        item.put("vehicleId", v.getId());
                        item.put("vehicleTitle", v.getTitle());
                        item.put("destination", v.getDestination());
                        item.put("vehicleDate", v.getVehicleDate());
                        item.put("passengerCount", v.getPassengerCount());
                        item.put("priority", v.getPriority());
                    }
                }
            }
            result.add(item);
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 查询当前用户在指定流程实例中的任务
     */
    private List<Task> findTasks(String processInstanceId, Long userId) {
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee(String.valueOf(userId))
                .list();
        if (tasks.isEmpty()) {
            tasks = processEngine.getTaskService().createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .taskCandidateUser(String.valueOf(userId))
                    .list();
        }
        return tasks;
    }

    /**
     * 保存审批历史记录
     */
    private void saveHistory(Long vehicleId, String taskId, String taskName,
                             Long assigneeId, String assigneeName,
                             String action, String comment, LocalDateTime endTime) {
        SysVehicleHistory history = new SysVehicleHistory();
        history.setVehicleId(vehicleId);
        history.setTaskId(taskId);
        history.setTaskName(taskName);
        history.setAssigneeId(assigneeId);
        history.setAssigneeName(assigneeName);
        history.setAction(action);
        history.setComment(comment);
        history.setStartTime(endTime.minusMinutes(5)); // 假设每步约5分钟
        history.setEndTime(endTime);
        vehicleHistoryMapper.insert(history);
    }
}
