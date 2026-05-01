package me.sunlianhui.coinflow.account;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import me.sunlianhui.coinflow.modules.category.model.AccountCategoryModel;
import me.sunlianhui.coinflow.modules.category.service.AccountCategoryService;

@SpringBootTest
public class AccountCategoryTest2 {

	@Autowired private AccountCategoryService categoryService;

	@Autowired private SnowflakeIdGenerator snowflakeIdGenerator;

	@Test
	void testAddCategory() {
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
