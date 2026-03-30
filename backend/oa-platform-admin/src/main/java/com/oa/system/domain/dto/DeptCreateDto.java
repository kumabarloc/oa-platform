package com.oa.system.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 部门创建DTO
 */
@Data
public class DeptCreateDto {
    private Long parentId = 0L;
    
    @NotBlank(message = "部门名称不能为空")
    private String deptName;
    
    private String deptCode;
    private String deptType;
    private Integer sortOrder;
    private Long leader;
    private String phone;
    private String email;
}