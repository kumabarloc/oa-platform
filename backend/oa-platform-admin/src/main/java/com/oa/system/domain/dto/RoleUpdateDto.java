package com.oa.system.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 角色更新DTO
 */
@Data
public class RoleUpdateDto {
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色权限不能为空")
    private String roleKey;

    private Integer roleSort;
    private String dataScope;
    private Integer menuCheckStrictly;
    private Integer deptCheckStrictly;
    private Integer status;
}