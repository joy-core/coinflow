package me.sunlianhui.coinflow.modules.notify.service;

import me.sunlianhui.coinflow.modules.notify.model.NotifyModel;
import java.util.List;

public interface NotifyService {

    List<NotifyModel> listByUserId(Long userId);
    void markAsRead(Long id);
    void markAllAsRead(Long userId);
    void deleteById(Long id);
    Long getUnreadCount(Long userId);

}
