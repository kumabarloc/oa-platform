package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notification_read")
public class SysNotificationRead {
    private Long id;
    private Long notificationId;
    private Long userId;
    private LocalDateTime readTime;
}
