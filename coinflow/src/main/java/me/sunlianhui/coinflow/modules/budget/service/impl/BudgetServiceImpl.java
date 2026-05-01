package me.sunlianhui.coinflow.modules.budget.service.impl;

import me.sunlianhui.coinflow.modules.budget.entity.BudgetEntity;
import me.sunlianhui.coinflow.modules.budget.mapper.BudgetMapper;
import me.sunlianhui.coinflow.modules.budget.model.BudgetModel;
import me.sunlianhui.coinflow.modules.budget.service.BudgetService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetMapper budgetMapper;

    @Override
    public List<BudgetModel> listBudgets(Map<String, Object> params) {
        List<BudgetEntity> entities = budgetMapper.selectAll();
        return entities.stream()
                .filter(entity -> entity.getUserId().equals(params.get("userId")))
                .map(entity -> {
                    BudgetModel model = new BudgetModel();
                    BeanUtils.copyProperties(entity, model);
                    return model;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BudgetModel add(BudgetModel model) {
        BudgetEntity entity = new BudgetEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        budgetMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public BudgetModel update(BudgetModel model) {
        BudgetEntity entity = new BudgetEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdatedAt(new Date());
        budgetMapper.update(entity);
        return model;
    }

    @Override
    public void deleteById(Long id) {
        budgetMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> getStatus(Map<String, Object> params) {
        // Simple implementation, return empty status data
        return Map.of();
    }

}
