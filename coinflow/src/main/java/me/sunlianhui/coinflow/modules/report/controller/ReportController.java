package me.sunlianhui.coinflow.modules.report.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/overview")
    public R getOverview(@RequestParam Map<String, Object> params) {
        return R.success(reportService.getOverview(params));
    }

    @GetMapping("/category")
    public R getCategoryStats(@RequestParam Map<String, Object> params) {
        return R.success(reportService.getCategoryStats(params));
    }

    @GetMapping("/monthly")
    public R getMonthlyStats(@RequestParam Map<String, Object> params) {
        return R.success(reportService.getMonthlyStats(params));
    }

    @GetMapping("/custom")
    public R getCustomReport(@RequestParam Map<String, Object> params) {
        return R.success(reportService.getCustomReport(params));
    }

}
