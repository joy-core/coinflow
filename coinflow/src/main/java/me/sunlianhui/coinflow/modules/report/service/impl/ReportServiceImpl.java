package me.sunlianhui.coinflow.modules.report.service.impl;

import me.sunlianhui.coinflow.modules.asset.mapper.AssetAccountMapper;
import me.sunlianhui.coinflow.modules.bill.entity.BillEntity;
import me.sunlianhui.coinflow.modules.bill.mapper.BillMapper;
import me.sunlianhui.coinflow.modules.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private AssetAccountMapper assetAccountMapper;

    @Override
    public Map<String, Object> getOverview(Map<String, Object> params) {
        Long userId = Long.valueOf(params.getOrDefault("userId", 1L).toString());
        String period = (String) params.getOrDefault("period", "month");

        LocalDate start = getPeriodStart(period);
        LocalDate end = getPeriodEnd(period);
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        LocalDate prevStart = start.minusDays(daysBetween);
        LocalDate prevEnd = start.minusDays(1);

        String startStr = formatDate(start);
        String endStr = formatDate(end);
        String prevStartStr = formatDate(prevStart);
        String prevEndStr = formatDate(prevEnd);

        BigDecimal income = billMapper.selectTotalAmountByUserIdAndTypeAndMonth(userId, "INCOME", startStr, endStr);
        BigDecimal expense = billMapper.selectTotalAmountByUserIdAndTypeAndMonth(userId, "EXPENSE", startStr, endStr);
        BigDecimal prevIncome = billMapper.selectTotalAmountByUserIdAndTypeAndMonth(userId, "INCOME", prevStartStr, prevEndStr);
        BigDecimal prevExpense = billMapper.selectTotalAmountByUserIdAndTypeAndMonth(userId, "EXPENSE", prevStartStr, prevEndStr);

        long billCount = billMapper.selectCountByUserIdAndMonth(userId, startStr, endStr);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalIncome", income);
        result.put("totalExpense", expense);
        result.put("balance", income.subtract(expense));
        result.put("incomeChange", calcChange(prevIncome, income));
        result.put("expenseChange", calcChange(prevExpense, expense));
        result.put("billCount", billCount);
        return result;
    }

    @Override
    public Map<String, Object> getCategoryStats(Map<String, Object> params) {
        Long userId = Long.valueOf(params.getOrDefault("userId", 1L).toString());
        String type = (String) params.getOrDefault("type", "EXPENSE");
        String period = (String) params.getOrDefault("period", "month");

        LocalDate start = getPeriodStart(period);
        LocalDate end = getPeriodEnd(period);
        String startStr = formatDate(start);
        String endStr = formatDate(end);

        List<Map<String, Object>> stats = billMapper.selectCategoryStatsByUserIdAndMonth(userId, type, startStr, endStr);
        return Map.of("type", type, "period", period, "data", stats);
    }

    @Override
    public Map<String, Object> getMonthlyStats(Map<String, Object> params) {
        Long userId = Long.valueOf(params.getOrDefault("userId", 1L).toString());
        int months = Integer.parseInt(params.getOrDefault("months", 6).toString());

        List<Map<String, Object>> rawStats = billMapper.selectMonthlyStatsByUserId(userId, null, months);

        List<String> monthsList = new ArrayList<>();
        List<BigDecimal> incomeList = new ArrayList<>();
        List<BigDecimal> expenseList = new ArrayList<>();
        for (Map<String, Object> row : rawStats) {
            monthsList.add(row.get("month").toString());
            incomeList.add(new BigDecimal(row.get("income").toString()));
            expenseList.add(new BigDecimal(row.get("expense").toString()));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("months", monthsList);
        result.put("income", incomeList);
        result.put("expense", expenseList);
        return result;
    }

    @Override
    public Map<String, Object> getCustomReport(Map<String, Object> params) {
        Long userId = Long.valueOf(params.getOrDefault("userId", 1L).toString());
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        if (startDate == null || endDate == null) {
            return Map.of("error", "startDate and endDate are required");
        }

        BigDecimal income = billMapper.selectTotalAmountByUserIdAndTypeAndMonth(userId, "INCOME", startDate, endDate);
        BigDecimal expense = billMapper.selectTotalAmountByUserIdAndTypeAndMonth(userId, "EXPENSE", startDate, endDate);
        long count = billMapper.selectCountByUserIdAndMonth(userId, startDate, endDate);

        List<Map<String, Object>> incomeStats = billMapper.selectCategoryStatsByUserIdAndMonth(userId, "INCOME", startDate, endDate);
        List<Map<String, Object>> expenseStats = billMapper.selectCategoryStatsByUserIdAndMonth(userId, "EXPENSE", startDate, endDate);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalIncome", income);
        result.put("totalExpense", expense);
        result.put("billCount", count);
        result.put("incomeByCategory", incomeStats);
        result.put("expenseByCategory", expenseStats);
        return result;
    }

    private LocalDate getPeriodStart(String period) {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        return switch (period) {
            case "month" -> now.with(TemporalAdjusters.firstDayOfMonth());
            case "quarter" -> now.withDayOfMonth(1).withMonth(((now.getMonthValue() - 1) / 3 * 3) + 1);
            case "year" -> now.with(TemporalAdjusters.firstDayOfYear());
            default -> now.with(TemporalAdjusters.firstDayOfMonth());
        };
    }

    private LocalDate getPeriodEnd(String period) {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        return switch (period) {
            case "month" -> now.with(TemporalAdjusters.lastDayOfMonth());
            case "quarter" -> now.withDayOfMonth(1).withMonth(((now.getMonthValue() - 1) / 3 * 3) + 3).with(TemporalAdjusters.lastDayOfMonth());
            case "year" -> now.with(TemporalAdjusters.lastDayOfYear());
            default -> now.with(TemporalAdjusters.lastDayOfMonth());
        };
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private BigDecimal calcChange(BigDecimal prev, BigDecimal curr) {
        if (prev.compareTo(BigDecimal.ZERO) == 0) {
            return curr.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
        }
        return curr.subtract(prev).divide(prev, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }
}
