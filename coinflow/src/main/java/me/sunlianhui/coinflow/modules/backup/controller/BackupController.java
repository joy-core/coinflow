package me.sunlianhui.coinflow.modules.backup.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.backup.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/export")
    public R exportData(@RequestParam(defaultValue = "1") Long userId) {
        return R.success(backupService.exportAllData(userId));
    }

    @PostMapping("/import")
    public R importData(@RequestBody String jsonData,
                        @RequestParam(defaultValue = "1") Long userId) {
        backupService.importAllData(jsonData, userId);
        return R.success();
    }
}
