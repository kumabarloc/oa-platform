package com.oa.system.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 部门更新DTO
 */
@Data
public class DeptUpdateDto {
    private Long parentId = 0L;
    
    @NotBlank(message = "部门名称不能为空")
    private String deptName;
    
    private String deptCode;
    private String deptType;
    private Integer sortOrder;
    private Long leader;
    private String phone;
    private String email;
    private Integer status;
}