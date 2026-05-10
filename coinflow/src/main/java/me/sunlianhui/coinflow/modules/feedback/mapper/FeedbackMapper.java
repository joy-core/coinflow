package me.sunlianhui.coinflow.modules.feedback.mapper;

import me.sunlianhui.coinflow.modules.feedback.entity.FeedbackEntity;
import java.util.List;

public interface FeedbackMapper {

    List<FeedbackEntity> selectByUserId(Long userId);
    FeedbackEntity selectById(Long id);
    void insert(FeedbackEntity entity);
    void update(FeedbackEntity entity);
    void deleteById(Long id);

}
