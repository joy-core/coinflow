package me.sunlianhui.coinflow.modules.accountbook.model;

import lombok.Data;

@Data
public class AccountBookModel {

    private Long id;
    private Long userId;
    private String name;
    private String icon;
    private String color;
    private Boolean isDefault;
    private Boolean isArchived;
}