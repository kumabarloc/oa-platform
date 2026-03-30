package com.oa.system.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 角色详情VO
 */
@Data
public class RoleDetailVo {
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private String dataScope;
    private Integer menuCheckStrictly;
    private Integer deptCheckStrictly;
    private Integer status;
    private Long[] menuIds;
}