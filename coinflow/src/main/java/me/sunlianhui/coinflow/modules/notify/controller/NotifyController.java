package me.sunlianhui.coinflow.modules.notify.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.notify.model.NotifyModel;
import me.sunlianhui.coinflow.modules.notify.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @GetMapping
    public R listNotifications(@RequestParam(defaultValue = "1") Long userId) {
        List<NotifyModel> list = notifyService.listByUserId(userId);
        return R.success(list);
    }

    @PutMapping("/{id}/read")
    public R markAsRead(@PathVariable Long id) {
        notifyService.markAsRead(id);
        return R.success();
    }

    @PutMapping("/read-all")
    public R markAllAsRead(@RequestParam Long userId) {
        notifyService.markAllAsRead(userId);
        return R.success();
    }

    @GetMapping("/unread-count")
    public R getUnreadCount(@RequestParam(defaultValue = "1") Long userId) {
        return R.success(notifyService.getUnreadCount(userId));
    }

    @DeleteMapping("/{id}")
    public R deleteNotification(@PathVariable Long id) {
        notifyService.deleteById(id);
        return R.success();
    }

}
