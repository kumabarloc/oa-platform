package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    private Long id;
    private String title;
    private String businessType;
    private String method;
    private String requestMethod;
    private String operatorType;
    private Long operId;
    private String operName;
    private String deptName;
    private String url;
    private String ip;
    private String location;
    private String operParam;
    private String jsonResult;
    private Integer status;
    private String errorMsg;
    private LocalDateTime operTime;
}
