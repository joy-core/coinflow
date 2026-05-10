package me.sunlianhui.coinflow.modules.notify.service.impl;

import me.sunlianhui.coinflow.modules.notify.entity.NotifyEntity;
import me.sunlianhui.coinflow.modules.notify.mapper.NotifyMapper;
import me.sunlianhui.coinflow.modules.notify.model.NotifyModel;
import me.sunlianhui.coinflow.modules.notify.service.NotifyService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private NotifyMapper notifyMapper;

    @Override
    public List<NotifyModel> listByUserId(Long userId) {
        List<NotifyEntity> entities = notifyMapper.selectByUserId(userId);
        return entities.stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long id) {
        NotifyEntity entity = notifyMapper.selectById(id);
        if (entity != null) {
            entity.setIsRead(true);
            notifyMapper.update(entity);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        notifyMapper.markAllReadByUserId(userId);
    }

    @Override
    public void deleteById(Long id) {
        notifyMapper.deleteById(id);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notifyMapper.selectUnreadCountByUserId(userId);
    }

    private NotifyModel entityToModel(NotifyEntity entity) {
        NotifyModel model = new NotifyModel();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

}
