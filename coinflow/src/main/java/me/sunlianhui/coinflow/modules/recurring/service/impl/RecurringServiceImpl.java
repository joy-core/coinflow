package me.sunlianhui.coinflow.modules.recurring.service.impl;

import me.sunlianhui.coinflow.modules.recurring.entity.RecurringEntity;
import me.sunlianhui.coinflow.modules.recurring.mapper.RecurringMapper;
import me.sunlianhui.coinflow.modules.recurring.model.RecurringModel;
import me.sunlianhui.coinflow.modules.recurring.service.RecurringService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class RecurringServiceImpl implements RecurringService {

    @Autowired
    private RecurringMapper recurringMapper;

    @Override
    public List<RecurringModel> listByUserId(Long userId) {
        List<RecurringEntity> entities = recurringMapper.selectAll();
        return entities.stream()
                .filter(entity -> entity.getUserId().equals(userId))
                .map(entity -> {
                    RecurringModel model = new RecurringModel();
                    BeanUtils.copyProperties(entity, model);
                    return model;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RecurringModel add(RecurringModel model) {
        RecurringEntity entity = new RecurringEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        recurringMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public RecurringModel update(RecurringModel model) {
        RecurringEntity entity = new RecurringEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdatedAt(new Date());
        recurringMapper.update(entity);
        return model;
    }

    @Override
    public void deleteById(Long id) {
        recurringMapper.deleteById(id);
    }

    @Override
    public void toggleStatus(Long id, Boolean status) {
        RecurringEntity entity = recurringMapper.selectById(id);
        if (entity != null) {
            entity.setIsActive(status);
            entity.setUpdatedAt(new Date());
            recurringMapper.update(entity);
        }
    }

}
