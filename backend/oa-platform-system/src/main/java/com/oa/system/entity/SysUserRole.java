package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户和角色关联实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_role")
public class SysUserRole {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 角色ID */
    private Long roleId;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private java.time.LocalDateTime createTime;
}