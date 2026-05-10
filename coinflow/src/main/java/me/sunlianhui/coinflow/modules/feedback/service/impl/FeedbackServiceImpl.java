package me.sunlianhui.coinflow.modules.feedback.service.impl;

import me.sunlianhui.coinflow.modules.feedback.entity.FeedbackEntity;
import me.sunlianhui.coinflow.modules.feedback.mapper.FeedbackMapper;
import me.sunlianhui.coinflow.modules.feedback.model.FeedbackModel;
import me.sunlianhui.coinflow.modules.feedback.service.FeedbackService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public List<FeedbackModel> listByUserId(Long userId) {
        List<FeedbackEntity> entities = feedbackMapper.selectByUserId(userId);
        return entities.stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackModel create(FeedbackModel model) {
        FeedbackEntity entity = new FeedbackEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setStatus("PENDING");
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        feedbackMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    private FeedbackModel entityToModel(FeedbackEntity entity) {
        FeedbackModel model = new FeedbackModel();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

}
