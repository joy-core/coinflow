package me.sunlianhui.coinflow.modules.notify.model;

import lombok.Data;
import java.util.Date;

@Data
public class NotifyModel {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Boolean isRead;
    private Date createdAt;
}
