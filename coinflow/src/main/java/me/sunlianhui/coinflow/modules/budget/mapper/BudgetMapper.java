package me.sunlianhui.coinflow.modules.budget.mapper;

import me.sunlianhui.coinflow.modules.budget.entity.BudgetEntity;
import java.util.List;

public interface BudgetMapper {

    List<BudgetEntity> selectAll();
    BudgetEntity selectById(Long id);
    void insert(BudgetEntity entity);
    void update(BudgetEntity entity);
    void deleteById(Long id);

    java.util.List<BudgetEntity> selectByUserId(@org.apache.ibatis.annotations.Param("userId") Long userId);

}
