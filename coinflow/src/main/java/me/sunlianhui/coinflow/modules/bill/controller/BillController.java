package me.sunlianhui.coinflow.modules.bill.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.bill.model.BillModel;
import me.sunlianhui.coinflow.modules.bill.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public R listBills(@RequestParam Map<String, Object> params) {
        return R.success(billService.listBills(params));
    }

    @PostMapping
    public R addBill(@RequestBody BillModel model) {
        return R.success(billService.add(model));
    }

    @PutMapping
    public R updateBill(@RequestBody BillModel model) {
        return R.success(billService.update(model));
    }

    @DeleteMapping("/{id}")
    public R deleteBillById(@PathVariable Long id) {
        billService.deleteById(id);
        return R.success();
    }

    @GetMapping("/statistics")
    public R getBillStatistics(@RequestParam Map<String, Object> params) {
        return R.success(billService.getStatistics(params));
    }

    @GetMapping("/recent")
    public R getRecentBills(@RequestParam(defaultValue = "1") Long userId,
                            @RequestParam(defaultValue = "10") int limit) {
        return R.success(billService.getRecentBills(userId, limit));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBills(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(defaultValue = "xlsx") String format) throws IOException {
        var bills = billService.listBillsForExport(userId, startDate, endDate);
        byte[] data;
        String filename;
        MediaType contentType;

        if ("csv".equalsIgnoreCase(format)) {
            data = billService.exportToCsv(bills);
            filename = "bills.csv";
            contentType = MediaType.parseMediaType("text/csv");
        } else {
            data = billService.exportToExcel(bills);
            filename = "bills.xlsx";
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(data);
    }

}
