package com.oa.system.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 角色创建DTO
 */
@Data
public class RoleCreateDto {
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;

    @NotBlank(message = "角色权限不能为空")
    @Size(max = 100, message = "角色权限长度不能超过100个字符")
    private String roleKey;

    private Integer roleSort;
    private String dataScope = "1";
    private Integer menuCheckStrictly = 1;
    private Integer deptCheckStrictly = 1;
}