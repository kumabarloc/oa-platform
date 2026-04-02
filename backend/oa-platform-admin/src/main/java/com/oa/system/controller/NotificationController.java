package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.system.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/system/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/list")
    public R<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String title) {
        return R.ok(notificationService.getList(pageNum, pageSize, type, title));
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(notificationService.getDetail(id));
    }

    @PostMapping
    public R<?> create(@RequestBody Map<String, Object> params) {
        return R.ok(notificationService.create(params));
    }

    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        notificationService.update(id, params);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return R.ok();
    }

    @PutMapping("/{id}/read")
    public R<?> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return R.ok();
    }

    @PutMapping("/read-all")
    public R<?> markAllRead() {
        notificationService.markAllRead();
        return R.ok();
    }

    @GetMapping("/my")
    public R<?> myList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(notificationService.getMyNotifications(pageNum, pageSize));
    }

    @GetMapping("/unread-count")
    public R<?> unreadCount() {
        return R.ok(notificationService.getUnreadCount());
    }
}
