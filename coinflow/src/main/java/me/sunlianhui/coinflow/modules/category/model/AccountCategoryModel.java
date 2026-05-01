package me.sunlianhui.coinflow.modules.category.model;

import lombok.Data;
import me.sunlianhui.coinflow.modules.category.entity.AccountCategoryEntity;

import java.util.Date;

@Data
public class AccountCategoryModel {
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

	// Entity -> Model (static factory method)
	public static AccountCategoryModel fromEntity(AccountCategoryEntity entity) {
		if (entity == null) {
			return null;
		}
		AccountCategoryModel model = new AccountCategoryModel();
		model.setId(entity.getId());
		model.setUserId(entity.getUserId());
		model.setAccountBookId(entity.getAccountBookId());
		model.setName(entity.getName());
		model.setType(entity.getType());
		model.setPid(entity.getPid());
		model.setIcon(entity.getIcon());
		model.setSortOrder(entity.getSortOrder());
		model.setCreatedAt(entity.getCreatedAt());
		model.setUpdatedAt(entity.getUpdatedAt());
		return model;
	}

	// Model -> Entity
	public AccountCategoryEntity toEntity() {
		AccountCategoryEntity entity = new AccountCategoryEntity();
		entity.setId(this.id);
		entity.setUserId(this.userId);
		entity.setAccountBookId(this.accountBookId);
		entity.setName(this.name);
		entity.setType(this.type);
		entity.setPid(this.pid);
		entity.setIcon(this.icon);
		entity.setSortOrder(this.sortOrder);
		entity.setCreatedAt(this.createdAt);
		entity.setUpdatedAt(this.updatedAt);
		return entity;
	}
}
