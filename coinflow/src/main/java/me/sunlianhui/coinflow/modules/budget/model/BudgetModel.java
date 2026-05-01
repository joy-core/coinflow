package me.sunlianhui.coinflow.modules.budget.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetModel {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private Long categoryId;
    private String month;
    private BigDecimal amount;

}
