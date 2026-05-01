package me.sunlianhui.coinflow.modules.accountbook.entity;

import lombok.Data;
import java.util.Date;

@Data
public class AccountBookEntity {

    private Long id;
    private Long userId;
    private String name;
    private String icon;
    private String color;
    private Boolean isDefault;
    private Boolean isArchived;
    private Date createdAt;
    private Date updatedAt;
}