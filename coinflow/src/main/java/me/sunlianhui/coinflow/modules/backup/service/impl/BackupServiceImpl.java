package me.sunlianhui.coinflow.modules.backup.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sunlianhui.coinflow.modules.accountbook.entity.AccountBookEntity;
import me.sunlianhui.coinflow.modules.accountbook.mapper.AccountBookMapper;
import me.sunlianhui.coinflow.modules.asset.entity.AssetAccountEntity;
import me.sunlianhui.coinflow.modules.asset.mapper.AssetAccountMapper;
import me.sunlianhui.coinflow.modules.backup.service.BackupService;
import me.sunlianhui.coinflow.modules.bill.entity.BillEntity;
import me.sunlianhui.coinflow.modules.bill.mapper.BillMapper;
import me.sunlianhui.coinflow.modules.budget.entity.BudgetEntity;
import me.sunlianhui.coinflow.modules.budget.mapper.BudgetMapper;
import me.sunlianhui.coinflow.modules.category.entity.AccountCategoryEntity;
import me.sunlianhui.coinflow.modules.category.mapper.AccountCategoryMapper;
import me.sunlianhui.coinflow.modules.recurring.entity.RecurringEntity;
import me.sunlianhui.coinflow.modules.recurring.mapper.RecurringMapper;
import me.sunlianhui.coinflow.modules.template.entity.TemplateEntity;
import me.sunlianhui.coinflow.modules.template.mapper.TemplateMapper;
import me.sunlianhui.coinflow.modules.transferrecord.entity.TransferRecordEntity;
import me.sunlianhui.coinflow.modules.transferrecord.mapper.TransferRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BackupServiceImpl implements BackupService {

    @Autowired
    private BillMapper billMapper;
    @Autowired
    private AccountCategoryMapper categoryMapper;
    @Autowired
    private AssetAccountMapper assetAccountMapper;
    @Autowired
    private AccountBookMapper accountBookMapper;
    @Autowired
    private BudgetMapper budgetMapper;
    @Autowired
    private RecurringMapper recurringMapper;
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private TransferRecordMapper transferRecordMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> exportAllData(Long userId) {
        return Map.of(
                "bills", billMapper.selectByUserIdWithFilters(userId, null, null),
                "categories", categoryMapper.selectByUserId(userId),
                "accounts", assetAccountMapper.selectByUserId(userId),
                "accountBooks", accountBookMapper.selectByUserId(userId),
                "budgets", budgetMapper.selectByUserId(userId),
                "recurringRules", recurringMapper.selectByUserId(userId),
                "templates", templateMapper.selectByUserId(userId),
                "transferRecords", transferRecordMapper.selectByUserId(userId)
        );
    }

    @Override
    @Transactional
    public void importAllData(String jsonData, Long userId) {
        try {
            Map<String, Object> data = objectMapper.readValue(jsonData, new TypeReference<>() {});

            // Import account books
            importList(data.get("accountBooks"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, AccountBookEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    entity.setCreatedAt(new Date());
                    entity.setUpdatedAt(new Date());
                    accountBookMapper.insert(entity);
                }
            });

            // Import categories
            importList(data.get("categories"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, AccountCategoryEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    categoryMapper.insert(entity);
                }
            });

            // Import asset accounts
            importList(data.get("accounts"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, AssetAccountEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    entity.setCreatedAt(new Date());
                    entity.setUpdatedAt(new Date());
                    assetAccountMapper.insert(entity);
                }
            });

            // Import bills
            importList(data.get("bills"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, BillEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    entity.setCreatedAt(new Date());
                    entity.setUpdatedAt(new Date());
                    billMapper.insert(entity);
                }
            });

            // Import budgets
            importList(data.get("budgets"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, BudgetEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    entity.setCreatedAt(new Date());
                    entity.setUpdatedAt(new Date());
                    budgetMapper.insert(entity);
                }
            });

            // Import recurring rules
            importList(data.get("recurringRules"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, RecurringEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    entity.setCreatedAt(new Date());
                    entity.setUpdatedAt(new Date());
                    recurringMapper.insert(entity);
                }
            });

            // Import templates
            importList(data.get("templates"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, TemplateEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    entity.setCreatedAt(new Date());
                    entity.setUpdatedAt(new Date());
                    templateMapper.insert(entity);
                }
            });

            // Import transfer records
            importList(data.get("transferRecords"), userId, list -> {
                for (Map<String, Object> m : list) {
                    var entity = objectMapper.convertValue(m, TransferRecordEntity.class);
                    entity.setId(null);
                    entity.setUserId(userId);
                    entity.setCreatedAt(new Date());
                    transferRecordMapper.insert(entity);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to import backup data: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void importList(Object data, Long userId, java.util.function.Consumer<List<Map<String, Object>>> action) {
        if (data == null) return;
        List<Map<String, Object>> list = objectMapper.convertValue(data, new TypeReference<>() {});
        if (list == null || list.isEmpty()) return;
        action.accept(list);
    }
}
