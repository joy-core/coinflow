package me.sunlianhui.coinflow.modules.budget.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BudgetEntity {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private Long categoryId;
    private String month;
    private BigDecimal amount;
    private Date createdAt;
    private Date updatedAt;

}
