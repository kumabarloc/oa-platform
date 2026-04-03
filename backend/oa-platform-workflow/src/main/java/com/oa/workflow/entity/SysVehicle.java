package com.oa.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用车申请表实体
 */
@Data
@TableName("sys_vehicle")
public class SysVehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请事由 */
    private String title;

    /** 详细说明 */
    private String reason;

    /** 用车日期 */
    private LocalDate vehicleDate;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 目的地 */
    private String destination;

    /** 乘车人数 */
    private Integer passengerCount;

    /** 状态：draft-草稿, pending-待审, approved-已通过, rejected-已驳回, completed-已完成 */
    private String status;

    /** 优先级 */
    private String priority;

    /** Flowable流程实例ID */
    private String processInstanceId;

    /** 当前环节 */
    private String currentStep;

    /** 申请人ID */
    private Long draftUserId;

    /** 申请人姓名 */
    private String draftUserName;

    /** 所属部门ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 创建部门 */
    @TableField(fill = FieldFill.INSERT)
    private Long createDept;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 删除标志 */
    @TableLogic
    private String delFlag;
}
