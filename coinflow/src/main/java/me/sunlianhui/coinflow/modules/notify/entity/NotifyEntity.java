package me.sunlianhui.coinflow.modules.notify.entity;

import lombok.Data;
import java.util.Date;

@Data
public class NotifyEntity {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Boolean isRead;
    private Date createdAt;
}
