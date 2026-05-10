package me.sunlianhui.coinflow.modules.feedback.model;

import lombok.Data;

@Data
public class FeedbackModel {
    private Long id;
    private Long userId;
    private String content;
    private String status;
}
