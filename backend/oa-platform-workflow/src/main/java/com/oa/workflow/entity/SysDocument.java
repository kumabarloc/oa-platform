package com.oa.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公文实体
 */
@Data
@TableName("sys_document")
public class SysDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 正文内容（富文本HTML） */
    private String content;

    /** 类型：notice-通知, info-简报, report-报告 */
    private String docType;

    /** 状态：draft-草稿, pending-待审, approved-已发布, rejected-已驳回 */
    private String status;

    /** 优先级：low-低, normal-普通, high-紧急 */
    private String priority;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 拟稿人ID */
    private Long draftUserId;

    /** 所属部门 */
    private Long deptId;

    /** 创建部门 */
    private Long createDept;

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;

    /** 删除标志 */
    @TableLogic
    private String delFlag;

    /** Flowable流程实例ID */
    private String processInstanceId;

    /** 当前审批步骤 */
    private String currentStep;
}
