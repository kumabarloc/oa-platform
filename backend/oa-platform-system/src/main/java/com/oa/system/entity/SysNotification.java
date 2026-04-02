package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notification")
public class SysNotification {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String category;
    private String sourceType;
    private String sourceId;
    private String priority;
    private Long senderId;
    private String senderName;
    private String targetType;
    private String targetId;
    private Long createDept;
    private Integer readStatus;
    private LocalDateTime readTime;
    @TableLogic
    private Integer delFlag;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
}
