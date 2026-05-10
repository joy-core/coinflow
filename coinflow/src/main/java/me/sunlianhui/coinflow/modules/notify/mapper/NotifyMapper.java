package me.sunlianhui.coinflow.modules.notify.mapper;

import me.sunlianhui.coinflow.modules.notify.entity.NotifyEntity;
import java.util.List;

public interface NotifyMapper {

    List<NotifyEntity> selectByUserId(Long userId);
    NotifyEntity selectById(Long id);
    void insert(NotifyEntity entity);
    void update(NotifyEntity entity);
    void deleteById(Long id);
    void markAllReadByUserId(Long userId);
    Long selectUnreadCountByUserId(Long userId);

}
