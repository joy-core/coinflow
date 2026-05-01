package me.sunlianhui.coinflow.modules.category.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AccountCategoryEntity {
	private Long id;
	private Long userId;
	private Long accountBookId;
	private String name;
	private String type; // INCOME or EXPENSE
	private Long pid;
	private String icon;
	private Integer sortOrder;
	private Date createdAt;
	private Date updatedAt;
}
