package me.sunlianhui.coinflow.modules.category.service;

import java.util.List;

import me.sunlianhui.coinflow.modules.category.model.AccountCategoryModel;

public interface AccountCategoryService {
	List<AccountCategoryModel> listByUserId(Long userId);

	AccountCategoryModel getById(Long id);

	void add(AccountCategoryModel category);

	void update(AccountCategoryModel category);

	void delete(Long id);
}
