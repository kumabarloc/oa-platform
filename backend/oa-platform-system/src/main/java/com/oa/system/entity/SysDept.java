package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.oa.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 部门ID */
    private Long id;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 部门编码 */
    private String deptCode;

    /** 部门类型 */
    private String deptType;

    /** 显示顺序 */
    private Integer sortOrder;

    /** 部门负责人用户ID */
    private Long leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门状态（0正常 1停用） */
    private Integer status;

    /** 子部门（树形查询用，非数据库字段） */
    @TableField(exist = false)
    private List<SysDept> children;

    /** 父部门名称（非数据库字段） */
    @TableField(exist = false)
    private String parentName;
}