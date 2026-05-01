package me.sunlianhui.coinflow.modules.budget.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.budget.model.BudgetModel;
import me.sunlianhui.coinflow.modules.budget.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public R listBudgets(@RequestParam Map<String, Object> params) {
        return R.success(budgetService.listBudgets(params));
    }

    @PostMapping
    public R addBudget(@RequestBody BudgetModel model) {
        return R.success(budgetService.add(model));
    }

    @PutMapping
    public R updateBudget(@RequestBody BudgetModel model) {
        return R.success(budgetService.update(model));
    }

    @DeleteMapping("/{id}")
    public R deleteBudgetById(@PathVariable Long id) {
        budgetService.deleteById(id);
        return R.success();
    }

    @GetMapping("/status")
    public R getBudgetStatus(@RequestParam Map<String, Object> params) {
        return R.success(budgetService.getStatus(params));
    }

}
