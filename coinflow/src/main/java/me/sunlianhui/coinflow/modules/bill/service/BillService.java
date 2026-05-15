package me.sunlianhui.coinflow.modules.bill.service;

import me.sunlianhui.coinflow.modules.bill.model.BillModel;
import java.util.List;
import java.util.Map;

public interface BillService {

    List<BillModel> listBills(Map<String, Object> params);
    BillModel add(BillModel model);
    BillModel update(BillModel model);
    void deleteById(Long id);
    Map<String, Object> getStatistics(Map<String, Object> params);
    List<BillModel> getRecentBills(Long userId, int limit);
    List<BillModel> listBillsForExport(Long userId, java.util.Date startDate, java.util.Date endDate);
    byte[] exportToExcel(List<BillModel> bills) throws java.io.IOException;
    byte[] exportToCsv(List<BillModel> bills) throws java.io.IOException;
    void toggleReimbursed(Long id, Boolean isReimbursed);
    List<java.util.Map<String, Object>> getTagStats(Long userId, java.util.Date startDate, java.util.Date endDate);

}
