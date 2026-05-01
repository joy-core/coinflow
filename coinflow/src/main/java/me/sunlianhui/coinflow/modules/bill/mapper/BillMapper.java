package me.sunlianhui.coinflow.modules.bill.mapper;

import me.sunlianhui.coinflow.modules.bill.entity.BillEntity;
import java.util.List;

public interface BillMapper {

    List<BillEntity> selectAll();
    BillEntity selectById(Long id);
    void insert(BillEntity entity);
    void update(BillEntity entity);
    void deleteById(Long id);

    java.math.BigDecimal selectTotalAmountByUserIdAndTypeAndMonth(
            Long userId, String type, String startDate, String endDate);

    long selectCountByUserIdAndMonth(Long userId, String startDate, String endDate);

    List<java.util.Map<String, Object>> selectCategoryStatsByUserIdAndMonth(
            Long userId, String type, String startDate, String endDate);

    List<java.util.Map<String, Object>> selectMonthlyStatsByUserId(
            Long userId, String type, int months);

    List<BillEntity> selectRecentByUserId(Long userId, int limit);

}
