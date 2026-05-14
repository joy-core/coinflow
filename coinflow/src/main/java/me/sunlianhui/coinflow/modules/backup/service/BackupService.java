package me.sunlianhui.coinflow.modules.backup.service;

import java.util.Map;

public interface BackupService {
    Map<String, Object> exportAllData(Long userId);
    void importAllData(String jsonData, Long userId);
}
