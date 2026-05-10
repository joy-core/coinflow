package me.sunlianhui.coinflow.modules.feedback.service;

import me.sunlianhui.coinflow.modules.feedback.model.FeedbackModel;
import java.util.List;

public interface FeedbackService {

    List<FeedbackModel> listByUserId(Long userId);
    FeedbackModel create(FeedbackModel model);

}
