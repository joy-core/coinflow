package me.sunlianhui.coinflow.modules.report.service;

import java.util.Map;

public interface ReportService {

    Map<String, Object> getOverview(Map<String, Object> params);
    Map<String, Object> getCategoryStats(Map<String, Object> params);
    Map<String, Object> getMonthlyStats(Map<String, Object> params);
    Map<String, Object> getCustomReport(Map<String, Object> params);

}
