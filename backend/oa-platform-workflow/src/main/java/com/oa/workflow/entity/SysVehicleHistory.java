package com.oa.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用车申请审批历史表
 */
@Data
@TableName("sys_vehicle_history")
public class SysVehicleHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用车申请ID */
    private Long vehicleId;

    /** Flowable任务ID */
    private String taskId;

    /** 任务节点名称 */
    private String taskName;

    /** 审批人ID */
    private Long assigneeId;

    /** 审批人姓名 */
    private String assigneeName;

    /** 操作：submit-提交, approve-通过, reject-驳回, complete-完成 */
    private String action;

    /** 审批意见 */
    private String comment;

    /** 节点开始时间 */
    private LocalDateTime startTime;

    /** 节点结束时间 */
    private LocalDateTime endTime;
}
