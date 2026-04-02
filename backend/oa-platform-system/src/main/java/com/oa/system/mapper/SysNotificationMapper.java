package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.SysNotification;
import com.oa.system.entity.SysNotificationRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysNotificationMapper extends BaseMapper<SysNotification> {
    List<SysNotification> selectMyNotifications(@Param("userId") Long userId, @Param("type") String type);
    void insertRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
    List<Long> selectReadIdsByUser(@Param("userId") Long userId);
    int countUnread(@Param("userId") Long userId);
}
