package me.sunlianhui.coinflow.modules.feedback.entity;

import lombok.Data;
import java.util.Date;

@Data
public class FeedbackEntity {
    private Long id;
    private Long userId;
    private String content;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
