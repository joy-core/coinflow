package me.sunlianhui.coinflow.modules.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.sunlianhui.coinflow.modules.category.entity.AccountCategoryEntity;

public interface AccountCategoryMapper {
	List<AccountCategoryEntity> selectByUserId(@Param("userId") Long userId);

	AccountCategoryEntity selectById(@Param("id") Long id);

	void insert(AccountCategoryEntity category);

	void update(AccountCategoryEntity category);

	void deleteById(@Param("id") Long id);
}
