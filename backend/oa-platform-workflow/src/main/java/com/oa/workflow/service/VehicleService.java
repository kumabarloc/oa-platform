package com.oa.workflow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.workflow.entity.SysVehicle;

import java.util.List;
import java.util.Map;

/**
 * 用车申请 Service 接口
 */
public interface VehicleService {

    /**
     * 分页列表
     */
    Page<SysVehicle> getVehicleList(int pageNum, int pageSize, String status, Long userId);

    /**
     * 详情（含历史记录）
     */
    Map<String, Object> getVehicleDetail(Long id);

    /**
     * 创建用车申请
     */
    Long createVehicle(SysVehicle data, Long userId, String userName);

    /**
     * 更新用车申请（仅草稿/驳回状态）
     */
    void updateVehicle(Long id, SysVehicle data);

    /**
     * 删除用车申请（仅草稿状态）
     */
    void deleteVehicle(Long id);

    /**
     * 提交启动 Flowable 流程
     */
    void submitVehicle(Long id, Long userId, String userName);

    /**
     * 审批通过
     */
    void approveVehicle(Long id, Long userId, String userName, String comment);

    /**
     * 驳回
     */
    void rejectVehicle(Long id, Long userId, String userName, String reason);

    /**
     * 我的待办任务列表
     */
    List<Map<String, Object>> getMyTasks(Long userId);
}
