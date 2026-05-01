package me.sunlianhui.coinflow.modules.budget.service;

import me.sunlianhui.coinflow.modules.budget.model.BudgetModel;
import java.util.List;
import java.util.Map;

public interface BudgetService {

    List<BudgetModel> listBudgets(Map<String, Object> params);
    BudgetModel add(BudgetModel model);
    BudgetModel update(BudgetModel model);
    void deleteById(Long id);
    Map<String, Object> getStatus(Map<String, Object> params);

}
