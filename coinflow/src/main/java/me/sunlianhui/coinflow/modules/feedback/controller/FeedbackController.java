package me.sunlianhui.coinflow.modules.feedback.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.feedback.model.FeedbackModel;
import me.sunlianhui.coinflow.modules.feedback.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public R listFeedback(@RequestParam(defaultValue = "1") Long userId) {
        List<FeedbackModel> list = feedbackService.listByUserId(userId);
        return R.success(list);
    }

    @PostMapping
    public R createFeedback(@RequestBody FeedbackModel model) {
        return R.success(feedbackService.create(model));
    }

}
