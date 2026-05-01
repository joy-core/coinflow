package me.sunlianhui.coinflow.account;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import me.sunlianhui.coinflow.modules.category.mapper.AccountCategoryMapper;
import me.sunlianhui.coinflow.modules.category.model.AccountCategoryModel;
import me.sunlianhui.coinflow.modules.category.service.impl.AccountCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AccountCategoryTest {

	@InjectMocks private AccountCategoryServiceImpl categoryService;

	@Mock private AccountCategoryMapper categoryMapper;

	@Mock private SnowflakeIdGenerator snowflakeIdGenerator;

	@Test
	public void testListByUserId() {
		// Mock user with ID 1
		Long userId = 1L;
		List<AccountCategoryModel> list = categoryService.listByUserId(userId);
		System.out.println(list);
		Assertions.assertEquals(0, list.size());
	}

	@Test
	public void testAddAccountCategory() {
		AccountCategoryModel category = new AccountCategoryModel();
		category.setId(snowflakeIdGenerator.nextId());
		category.setUserId(1L);
		category.setName("Daily Expenses");
		category.setType("income");
		category.setIcon("icon-test");
		category.setSortOrder(1);
		category.setPid(-1L);
		category.setCreatedAt(new Date());
		category.setAccountBookId(1L);
		categoryService.add(category);
		System.out.println(category);
	}

}
