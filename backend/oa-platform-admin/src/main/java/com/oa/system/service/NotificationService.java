package com.oa.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oa.common.core.exception.BusinessException;
import com.oa.common.core.page.PageResult;
import com.oa.common.core.result.ResultCode;
import com.oa.common.core.security.SecurityUtils;
import com.oa.system.entity.SysNotification;
import com.oa.system.mapper.SysNotificationMapper;
import com.oa.system.mapper.SysNotificationReadMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class NotificationService {

    private final SysNotificationMapper notificationMapper;
    private final SysNotificationReadMapper readMapper;

    public NotificationService(SysNotificationMapper notificationMapper,
                               SysNotificationReadMapper readMapper) {
        this.notificationMapper = notificationMapper;
        this.readMapper = readMapper;
    }

    public PageResult<Map<String, Object>> getList(int pageNum, int pageSize, String type, String title) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysNotification> list = notificationMapper.selectList(null);
        // Filter by type/title in memory for now (or add to mapper)
        List<SysNotification> filtered = list;
        if (type != null && !type.isEmpty()) {
            final String ftype = type;
            filtered = list.stream().filter(n -> ftype.equals(n.getType())).toList();
        }
        if (title != null && !title.isEmpty()) {
            final String ft = title;
            filtered = filtered.stream().filter(n -> n.getTitle() != null && n.getTitle().contains(ft)).toList();
        }
        PageInfo<SysNotification> pageInfo = new PageInfo<>(list);
        List<Map<String, Object>> result = new ArrayList<>();
        Long userId = SecurityUtils.getUserId();
        List<Long> readIds = userId != null ? notificationMapper.selectReadIdsByUser(userId) : Collections.emptyList();
        Set<Long> readSet = new HashSet<>(readIds);
        for (SysNotification n : filtered) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getId());
            m.put("title", n.getTitle());
            m.put("content", n.getContent());
            m.put("type", n.getType());
            m.put("priority", n.getPriority());
            m.put("senderName", n.getSenderName());
            m.put("targetType", n.getTargetType());
            m.put("readStatus", readSet.contains(n.getId()) ? 1 : 0);
            m.put("createTime", n.getCreateTime());
            result.add(m);
        }
        return PageResult.of(pageInfo.getTotal(), result, (long)pageNum, (long)pageSize);
    }

    public Map<String, Object> getDetail(Long id) {
        SysNotification n = notificationMapper.selectById(id);
        if (n == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        Map<String, Object> m = new HashMap<>();
        m.put("id", n.getId());
        m.put("title", n.getTitle());
        m.put("content", n.getContent());
        m.put("type", n.getType());
        m.put("priority", n.getPriority());
        m.put("senderName", n.getSenderName());
        m.put("targetType", n.getTargetType());
        m.put("createTime", n.getCreateTime());
        return m;
    }

    @Transactional
    public Long create(Map<String, Object> params) {
        SysNotification n = new SysNotification();
        n.setTitle((String) params.get("title"));
        n.setContent((String) params.get("content"));
        n.setType((String) params.getOrDefault("type", "system"));
        n.setPriority((String) params.getOrDefault("priority", "normal"));
        n.setTargetType((String) params.getOrDefault("targetType", "all"));
        n.setSenderId(SecurityUtils.getUserId());
        n.setSenderName(SecurityUtils.getUserName());
        n.setCreateDept(SecurityUtils.getDeptId() != null ? SecurityUtils.getDeptId() : 100L);
        n.setDelFlag(0);
        notificationMapper.insert(n);
        return n.getId();
    }

    @Transactional
    public void update(Long id, Map<String, Object> params) {
        SysNotification n = notificationMapper.selectById(id);
        if (n == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (params.get("title") != null) n.setTitle((String) params.get("title"));
        if (params.get("content") != null) n.setContent((String) params.get("content"));
        if (params.get("type") != null) n.setType((String) params.get("type"));
        notificationMapper.updateById(n);
    }

    @Transactional
    public void delete(Long id) {
        notificationMapper.deleteById(id);
    }

    @Transactional
    public void markRead(Long id) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return;
        notificationMapper.insertRead(id, userId);
    }

    @Transactional
    public void markAllRead() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return;
        List<SysNotification> all = notificationMapper.selectList(null);
        for (SysNotification n : all) {
            notificationMapper.insertRead(n.getId(), userId);
        }
    }

    public PageResult<Map<String, Object>> getMyNotifications(int pageNum, int pageSize) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return new PageResult<>(0L, Collections.emptyList(), (long)pageNum, (long)pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<SysNotification> list = notificationMapper.selectMyNotifications(userId, null);
        List<Map<String, Object>> result = new ArrayList<>();
        List<Long> readIds = notificationMapper.selectReadIdsByUser(userId);
        Set<Long> readSet = new HashSet<>(readIds);
        for (SysNotification n : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getId());
            m.put("title", n.getTitle());
            m.put("content", n.getContent());
            m.put("type", n.getType());
            m.put("priority", n.getPriority());
            m.put("senderName", n.getSenderName());
            m.put("readStatus", readSet.contains(n.getId()) ? 1 : 0);
            m.put("createTime", n.getCreateTime());
            result.add(m);
        }
        return PageResult.of((long)list.size(), result, (long)pageNum, (long)pageSize);
    }

    public int getUnreadCount() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return 0;
        return notificationMapper.countUnread(userId);
    }
}
