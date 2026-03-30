package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.oa.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色ID */
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色权限字符串 */
    private String roleKey;

    /** 显示顺序 */
    private Integer roleSort;

    /** 数据范围（1全部 2本部门及以下） */
    private String dataScope;

    /** 菜单树严格绑定 */
    private Integer menuCheckStrictly;

    /** 部门树严格绑定 */
    private Integer deptCheckStrictly;

    /** 角色状态（0正常 1停用） */
    private Integer status;
}