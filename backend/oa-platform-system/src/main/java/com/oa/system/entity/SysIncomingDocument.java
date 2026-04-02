package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_incoming_document")
public class SysIncomingDocument {
    private Long id;
    private String docNumber;
    private String sourceUnit;
    private LocalDateTime receiveTime;
    private String title;
    private String content;
    private String attachUrls;
    private String status;
    private Long currentHandlerId;
    private Long draftUserId;
    private String priority;
    private Long createBy;
    private Long createDept;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private String delFlag;
}
