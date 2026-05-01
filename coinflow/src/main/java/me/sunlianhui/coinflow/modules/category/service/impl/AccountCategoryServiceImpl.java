package me.sunlianhui.coinflow.modules.category.service.impl;

import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.sunlianhui.coinflow.modules.category.entity.AccountCategoryEntity;
import me.sunlianhui.coinflow.modules.category.mapper.AccountCategoryMapper;
import me.sunlianhui.coinflow.modules.category.model.AccountCategoryModel;
import me.sunlianhui.coinflow.modules.category.service.AccountCategoryService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;

@Service
public class AccountCategoryServiceImpl implements AccountCategoryService {

	@Autowired private AccountCategoryMapper mapper;

	@Override
	public List<AccountCategoryModel> listByUserId(Long userId) {
		List<AccountCategoryEntity> entities = mapper.selectByUserId(userId);
		return entities.stream().map(AccountCategoryModel::fromEntity).toList();
	}

	@Override
	public AccountCategoryModel getById(Long id) {
		AccountCategoryEntity entity = mapper.selectById(id);
		return AccountCategoryModel.fromEntity(entity);
	}

	@Override
	public void add(AccountCategoryModel category) {
		AccountCategoryEntity entity = category.toEntity();
		entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
		entity.setCreatedAt(new Date());
		entity.setUpdatedAt(new Date());
		mapper.insert(entity);
	}

	@Override
	public void update(AccountCategoryModel category) {
		AccountCategoryEntity entity = category.toEntity();
		entity.setUpdatedAt(new Date());
		mapper.update(entity);
	}

	@Override
	public void delete(Long id) {
		mapper.deleteById(id);
	}
}
